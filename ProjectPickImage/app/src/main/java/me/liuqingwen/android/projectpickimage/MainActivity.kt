package me.liuqingwen.android.projectpickimage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity()
{
    companion object
    {
        private const val PERMISSION_CAMERA_REQUEST_CODE = 100;
        private const val ACTION_CAMERA_REQUEST_CODE = 101;
        private const val ACTION_ALBUM_REQUEST_CODE = 102;
    }
    
    private val buttonClickHandler = View.OnClickListener { view ->
        when(view)
        {
            this.buttonCamera -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), MainActivity.PERMISSION_CAMERA_REQUEST_CODE)
                }else
                {
                    this.takeImageFromCamera()
                }
            }
            this.buttonPickImage -> {
                this.takeImageFromAlbum()
            }
            else -> {
                this.toast("Not implemented yet.")
            }
        }
    }
    
    private fun takeImageFromCamera()
    {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        this.startActivityForResult(intent, MainActivity.ACTION_CAMERA_REQUEST_CODE)
    }
    
    private fun takeImageFromAlbum()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        this.startActivityForResult(intent, MainActivity.ACTION_ALBUM_REQUEST_CODE)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)
        {
            MainActivity.ACTION_CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK)
                {
                    val uri = data?.data
                    if(uri == null)
                    {
                        val bundle = data?.extras
                        if (bundle != null)
                        {
                            val bitmap = bundle.get("data") as Bitmap
                            this.displayImage(bitmap)
                        } else
                        {
                            this.toast("No images found!")
                        }
                    } else
                    {
                        val bitmap = BitmapFactory.decodeFile(uri.path)
                        this.displayImage(bitmap)
                    }
                } else
                {
                    this.toast("User canceled!")
                }
            }
            MainActivity.ACTION_ALBUM_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK)
                {
                    val uri = data?.data
                    val resolver = this.contentResolver
                    val bitmap = MediaStore.Images.Media.getBitmap(resolver, uri)
                    if (bitmap != null)
                    {
                        this.displayImage(bitmap)
                    }
                    else
                    {
                        this.toast("No image data returns!")
                    }
                }else
                {
                    this.toast("User canceled!")
                }
            }
            else -> this.toast("Not implemented yet.")
        }
    }
    
    private fun displayImage(bitmap: Bitmap)
    {
        this.imagePhoto.setImageBitmap(bitmap)
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        if (requestCode == MainActivity.PERMISSION_CAMERA_REQUEST_CODE)
        {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
            {
                this.takeImageFromCamera()
            }else
            {
                this.toast("No permission granted for camera.")
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.buttonPickImage.setOnClickListener(this.buttonClickHandler)
        this.buttonCamera.setOnClickListener(this.buttonClickHandler)
    }
    
    
}
