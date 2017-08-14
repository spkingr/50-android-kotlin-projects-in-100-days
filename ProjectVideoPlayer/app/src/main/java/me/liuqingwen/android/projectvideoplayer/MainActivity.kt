package me.liuqingwen.android.projectvideoplayer

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Size
import android.view.SurfaceHolder
import android.view.View
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity()
{
    private val mediaPlayer by lazy(LazyThreadSafetyMode.NONE) { MediaPlayer() }
    private var mediaUri:Uri? = null
    private var isSurfaceViewReady = false
    private val clickHandler = { view:View ->
        when(view.id)
        {
            R.id.buttonPick -> { this.pickVideo() }
            R.id.buttonPlay -> { if (! this.mediaPlayer.isPlaying) this.playVideo() }
            R.id.buttonPause -> { if (this.mediaPlayer.isPlaying) this.mediaPlayer.pause() else this.mediaPlayer.start() }
            R.id.buttonStop -> {
                if (this.mediaPlayer.isPlaying)
                {
                    this.mediaPlayer.seekTo(0)
                    this.mediaPlayer.stop()
                }
            }
            else -> {this.toast("Not implemented yet!")}
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun pickVideo()
    {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        this.startActivityForResult(intent, 101)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101)
        {
            if (resultCode == AppCompatActivity.RESULT_OK)
            {
                val uri = data?.data
                this.mediaUri = uri
                if (this.isSurfaceViewReady)
                {
                    this.playVideo()
                }
            }
            else
            {
                this.toast("Action canceled.")
            }
        }
    }
    
    private fun init()
    {
        this.buttonPick.setOnClickListener(this.clickHandler)
        this.surfaceView.holder.addCallback(object:SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                holder?.let {
                    it.setKeepScreenOn(true)
                    it.setFormat(PixelFormat.TRANSPARENT)
                    this@MainActivity.mediaPlayer.setDisplay(it)
                }
                this@MainActivity.onSurfaceReady()
            }
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) { }
            override fun surfaceDestroyed(holder: SurfaceHolder?) { this@MainActivity.mediaPlayer.setDisplay(null) }
        })
    }
    
    private fun onSurfaceReady()
    {
        this.buttonPlay.setOnClickListener(this.clickHandler)
        this.buttonPause.setOnClickListener(this.clickHandler)
        this.buttonStop.setOnClickListener(this.clickHandler)
        
        this.isSurfaceViewReady = true
        if (this.mediaUri != null)
        {
            this.playVideo()
        }
    }
    
    private fun playVideo()
    {
        if (this.mediaUri == null)
        {
            this.toast("No video selected to play!")
            return
        }
        
        this.buttonPick.visibility = View.INVISIBLE
        try
        {
            this.mediaPlayer.reset()
            this.mediaPlayer.setDataSource(this, this.mediaUri)
            this.mediaPlayer.prepare()
            this.setupVideoSize()
            this.mediaPlayer.start()
        }
        catch(e: Exception)
        {
            this.mediaPlayer.setDisplay(null)
            this.buttonPick.visibility = View.VISIBLE
            this.toast("Error while playing.")
        }
    }
    
    private fun setupVideoSize()
    {
        val width = this.mediaPlayer.videoWidth
        val height = this.mediaPlayer.videoHeight
        
        val outSize = Point()
        this.windowManager.defaultDisplay.getSize(outSize)
        val videoHeight = outSize.x * height / width
        
        val layoutParams = ConstraintLayout.LayoutParams(outSize.x, videoHeight)
        this.surfaceView.layoutParams = layoutParams
    }
}
