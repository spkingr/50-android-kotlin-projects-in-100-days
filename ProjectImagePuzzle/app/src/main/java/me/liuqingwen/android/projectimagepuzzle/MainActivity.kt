package me.liuqingwen.android.projectimagepuzzle

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.AssetManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.layout_activity_main.*
import me.liuqingwen.android.projectimagepuzzle.util.MyGlideEngine
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.ctx
import pub.devrel.easypermissions.EasyPermissions

class MainActivity() : AppCompatActivity(), EasyPermissions.PermissionCallbacks, FragmentImageList.IFragmentInteractionListener, FragmentImagePuzzle.IFragmentInteractionListener, AnkoLogger
{
    companion object
    {
        const val ASSET_DIRECTORY_NAME = "sampleImages"
        const val PROVIDER_AUTHORITY = "me.liuqingwen.fileProvider"
        
        private const val REQUEST_CODE = 100
        private const val REQUEST_RESULT = 101
    }
    
    override val assetManager: AssetManager? get() = this.assets
    private val isHomeFragment get() = this.supportFragmentManager.backStackEntryCount == 0
    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    
    override fun onSelectImagePath(path: String)
    {
        val fragment = FragmentImagePuzzle.newInstance(path)
        this.supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, fragment)
                .commit()
    }
    
    private fun onSelectImageFromMedia(uri: Uri)
    {
        val fragment = FragmentImagePuzzle.newInstance(uri)
        this.supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, fragment)
                .commit()
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.title = "Image Puzzle"
        this.supportActionBar?.hide()
    
        this.buttonImage.setOnClickListener { this.chooseMedia() }
        this.buttonCamera.setOnClickListener { this.chooseMedia(true) }
        
        this.supportFragmentManager.addOnBackStackChangedListener {
            this.displayButtons(this.isHomeFragment)
        }
        
        this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, FragmentImageList.newInstance())
                .commit()
    }
    
    private fun chooseMedia(isCameraType: Boolean = false)
    {
        if (EasyPermissions.hasPermissions(this.ctx, *this.permissions))
        {
            this.onPermissionsGranted(MainActivity.REQUEST_CODE, arrayListOf())
        }
        else
        {
            EasyPermissions.requestPermissions(this, "Permissions for next step.", MainActivity.REQUEST_CODE, *this.permissions)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.REQUEST_RESULT && resultCode == Activity.RESULT_OK)
        {
            val result = Matisse.obtainResult(data)
            val uri = result.first()!!
            this.onSelectImageFromMedia(uri)
        }
    }
    
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>)
    {
        if (requestCode == MainActivity.REQUEST_CODE)
        {
            Toasty.error(this.ctx, "Permissions denied!", Toast.LENGTH_SHORT, true).show()
        }
    }
    
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>)
    {
        if (requestCode == MainActivity.REQUEST_CODE)
        {
            Matisse.from(this)
                    .choose(MimeType.of(MimeType.GIF, MimeType.JPEG, MimeType.PNG))
                    .capture(true)
                    .captureStrategy(CaptureStrategy(true, MainActivity.PROVIDER_AUTHORITY))
                    .countable(true)
                    .maxSelectable(1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.75f)
                    .imageEngine(MyGlideEngine())
                    .forResult(MainActivity.REQUEST_RESULT)
        }
    }
    
    private fun displayButtons(show: Boolean = true)
    {
        this.buttonImage.isVisible = show
        this.buttonCamera.isVisible = show
    }
}