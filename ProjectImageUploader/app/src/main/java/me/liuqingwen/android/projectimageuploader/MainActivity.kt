package me.liuqingwen.android.projectimageuploader

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.layout_activity_main.*
import kotlinx.android.synthetic.main.viewstub_image_description.view.*
import okhttp3.*
import org.jetbrains.anko.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity()
{
    
    companion object
    {
        const val COOKIE_EXTRA = "cookie_extra"
        
        private const val IMAGE_PICKER_REQUEST_CODE = 1001
        private const val IMAGE_CROP_REQUEST_CODE = 1002
        private const val PERMISSION_READ_WRITE_REQUEST_CODE = 1003
        
        //Parameter "file" is from the website api!!!
        private const val API_UPLOAD_URL = "http://liuqingwen.me/test/upload_file.php"
        private const val API_UPLOAD_PARAM = "file"
    }
    private lateinit var cookie:String
    private var isUploading = false
    private var isUploaded = false
    private var bitmapData:Bitmap? = null
    private var imageUri:Uri? = null
    private var filePath:String? = null
    private val httpClient by lazy(LazyThreadSafetyMode.NONE) { OkHttpClient() }
    private val viewInfo by lazy(LazyThreadSafetyMode.NONE) { this.viewStubDescription.inflate() }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.cookie = this.intent.getStringExtra(MainActivity.COOKIE_EXTRA)
        this.init()
    }
    
    private fun init()
    {
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.title = "Upload Image"
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        this.imagePhoto.setOnClickListener {
            this.takeImageFromAlbum()
        }
    
        this.buttonUpload.isEnabled = false
        this.buttonUpload.setOnClickListener {
            this.uploadImage()
        }
    }
    
    private fun uploadImage()
    {
        this.buttonUpload.isEnabled = false
        
        if (this.isUploading)
        {
            this.toast("Waiting before uploaded!")
            return
        }
        
        if (this.isUploaded)
        {
            this.toast("Already uploaded!")
            return
        }
    
        if (this.bitmapData == null)
        {
            this.toast("No image to upload.")
            return
        }
    
        this.isUploading = true
        val dialog = indeterminateProgressDialog("Uploading, please wait...", "Upload Image")
        dialog.show()
    
        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(MainActivity.API_UPLOAD_PARAM, this.filePath!!, RequestBody.create(MediaType.parse("image/jpeg"), File(this.filePath!!)))
                .build()
        val request = Request.Builder()
                .url(MainActivity.API_UPLOAD_URL)
                .addHeader("Cookie", this.cookie)
                .post(requestBody)
                .build()
    
        this.doAsync {
            val body = try
            {
                val response = this@MainActivity.httpClient.newCall(request).execute()
                if (response.isSuccessful) response.body()?.string() else null
            }catch (e:Exception)
            {
                null
            }
        
            uiThread {
                dialog.dismiss()
                
                this@MainActivity.isUploading = false
                this@MainActivity.imagePhoto.setImageBitmap(this@MainActivity.bitmapData)
                this@MainActivity.buttonUpload.isEnabled = true
            
                if(body != null)
                {
                    val info = Gson().fromJson<UploadResult>(body, UploadResult::class.java)
                    if (info.result)
                    {
                        this@MainActivity.supportActionBar?.title = "Uploaded Successfully"
                        this@MainActivity.isUploaded = true
                        
                        this@MainActivity.setImageInfo(info.author ?: "Null", info.url ?: "Null", info.date ?: "Null", info.description ?: "Null")
                    }
                    else
                    {
                        this@MainActivity.alert {
                            this.title = "Upload Failed"
                            this.message = info.info
                            positiveButton("Cancel") {  }
                        }.show()
                        this@MainActivity.supportActionBar?.title = "Uploaded Failed"
                    }
                }
                else
                {
                    this@MainActivity.alert {
                        this.title = "Upload Failed"
                        this.message = "Make sure your file is not deleted and your network works fine."
                        positiveButton("OK") {  }
                    }.show()
                    this@MainActivity.supportActionBar?.title = "Uploaded Failed"
                }
            }
        }
    }
    
    private fun setImageInfo(author:String, url:String, date:String, description:String)
    {
        this.viewInfo.visibility = View.VISIBLE
        
        this.viewInfo.labelAuthor.text = "Author: " + author
        this.viewInfo.labelUrlPath.text = "Url: " + url
        this.viewInfo.labelUploadDate.text = "Date: " + date
        this.viewInfo.labelDescription.text = "Information: \n" + description
    }
    
    private fun takeImageFromAlbum()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.type = "image/*"
        this.startActivityForResult(intent, MainActivity.IMAGE_PICKER_REQUEST_CODE)
    }
    
    private fun displayImage()
    {
        if (this.bitmapData != null)
        {
            if (this.isUploaded)
            {
                this.isUploaded = false
                this.viewInfo.visibility = View.INVISIBLE
            }
            this.buttonUpload.isEnabled = true
            this.supportActionBar?.title = "Upload Image"
            
            this.imagePhoto.setImageBitmap(this.bitmapData)
        }
        else
        {
            this.toast("No image data to display!")
        }
    }
    
    private fun cropImage()
    {
        this.imageUri?.let {
            val uri = getRealUri(this, this.imageUri)
            val intent = getCropIntent(uri)
            this.startActivityForResult(intent, MainActivity.IMAGE_CROP_REQUEST_CODE)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != AppCompatActivity.RESULT_OK)
        {
            this.toast("User canceled!")
            return
        }
        
        when(requestCode)
        {
            MainActivity.IMAGE_PICKER_REQUEST_CODE -> {
                this.imageUri = data?.data
                this.filePath = this.imageUri?.path
                
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), MainActivity.PERMISSION_READ_WRITE_REQUEST_CODE)
                }
                else
                {
                    this.cropImage()
                }
            }
            MainActivity.IMAGE_CROP_REQUEST_CODE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    this.filePath = data?.data?.path
                    val file = this.filePath?.let { FileInputStream(it) }
                    this.bitmapData = BitmapFactory.decodeStream(file)
                    this.displayImage()
                }
                else
                {
                    val extras = data?.extras
                    this.bitmapData = extras?.getParcelable("data")
                    this.displayImage()
                }
            }
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainActivity.PERMISSION_READ_WRITE_REQUEST_CODE)
        {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
            {
                this.cropImage()
            }
            else
            {
                this.bitmapData = MediaStore.Images.Media.getBitmap(this.contentResolver, this.imageUri)
                this.displayImage()
            }
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            android.R.id.home -> {
                this.onBackPressed()
            }
            else -> {
                this.toast("Not implemented yet!")
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onBackPressed()
    {
        if (this.isUploaded || this.bitmapData == null)
        {
            super.onBackPressed()
            return
        }
        
        alert {
            this.title = "Warning"
            this.message = "No image uploaded, are you sure to leave?"
            negativeButton("Discard") { super.onBackPressed() }
            positiveButton("Stay") {  }
        }.show()
    }
}
