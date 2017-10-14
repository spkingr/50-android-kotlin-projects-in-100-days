package me.liuqingwen.android.projectdownloaderwithnotification

import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.layout_activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.RandomAccessFile

enum class DownloadStatus
{
    READY, DOWNLOADING, PAUSED, CANCELED, COMPLETED, FAILED
}

class MainActivity : AppCompatActivity()
{
    companion object
    {
        const val DOWNLOAD_URL = "http://liuqingwen.me/upload/download/test.apk"
        const val FILE_NAME = "download_test.apk"
        const val CHANNEL_ID = "download"
        const val NOTIFICATION_ID = 1
    }
    
    private val file by lazy { File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + MainActivity.FILE_NAME) }
    private var downloadedSize = 0L
    private var downloadStatus = DownloadStatus.READY
    
    private val notificationManager by lazy { this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    
    private val client by lazy { OkHttpClient.Builder().build() }
    //private val request by lazy { Request.Builder().get().url(MainActivity.DOWNLOAD_URL).build() }
    
    private fun getNotification(title:String, progress:Int) = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID).apply {
        this.setSmallIcon(R.mipmap.ic_launcher)
        this.setLargeIcon(BitmapFactory.decodeResource(this@MainActivity.resources, R.mipmap.ic_launcher))
        this.setContentTitle(title)
        if (progress > 0)
        {
            this.setProgress(100, progress, false)
            this.setContentText("$progress%")
        }
    }.build()
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        val buttonClickHandler = { view:View ->
            when(view.id)
            {
                R.id.buttonStart -> this.startDownload()
                R.id.buttonPause -> this.pauseDownload()
                R.id.buttonStop -> this.stopDownload()
                R.id.buttonRemove -> this.removeDownloadedFile()
                else -> { toast("Not implemented yet!") }
            }
        }
        
        this.buttonStart.setOnClickListener(buttonClickHandler)
        this.buttonPause.setOnClickListener(buttonClickHandler)
        this.buttonStop.setOnClickListener(buttonClickHandler)
        this.buttonRemove.setOnClickListener(buttonClickHandler)
    }
    
    private fun startDownload()
    {
        if (this.downloadStatus == DownloadStatus.DOWNLOADING || this.downloadStatus == DownloadStatus.COMPLETED)
        {
            return
        }
        
        if (this.downloadStatus != DownloadStatus.PAUSED)
        {
            this.downloadStatus = DownloadStatus.READY
            this.labelStatus.text = "Ready..."
            this@MainActivity.progressDownload.progress = 0
            
            this.downloadedSize = if(this.file.exists()) this.file.length() else 0L
        }
        
        
        doAsync {
            val total = this@MainActivity.getContentLengthAsync()
            if (total <= 0)
            {
                this@MainActivity.downloadStatus = DownloadStatus.FAILED
                uiThread {
                    this@MainActivity.labelStatus.text = "Download failed for fetching content!"
                    this@MainActivity.progressDownload.progress = 0
                }
                return@doAsync
            }
            
            uiThread {
                this@MainActivity.labelStatus.text = "Downloading..."
                this@MainActivity.progressDownload.progress = (this@MainActivity.downloadedSize * 100 / total.toDouble()).toInt()
            }
            
            //should try catch here for execute?
            val request = Request.Builder().get().url(MainActivity.DOWNLOAD_URL).header("RANGE", "bytes=${this@MainActivity.downloadedSize}-").build()
            val response = this@MainActivity.client.newCall(request).execute()
            if (response != null && response.isSuccessful && response.body() != null)
            {
                this@MainActivity.downloadStatus = DownloadStatus.DOWNLOADING
                
                val randomAccessFile = RandomAccessFile(this@MainActivity.file, "rw")
                randomAccessFile.seek(this@MainActivity.downloadedSize)
                val stream = response.body()!!.byteStream()
                val bytes = ByteArray(1024)
                var readTotal = this@MainActivity.downloadedSize
                var bytesLength = stream.read(bytes)
                while (bytesLength != -1)
                {
                    when(this@MainActivity.downloadStatus)
                    {
                        DownloadStatus.PAUSED -> {
                            val progress = (readTotal * 100 / total.toDouble()).toInt()
                            this@MainActivity.notificationManager.notify(MainActivity.NOTIFICATION_ID, this@MainActivity.getNotification("Download paused!", progress))
                            return@doAsync
                        }
                        DownloadStatus.CANCELED -> {
                            this@MainActivity.notificationManager.notify(MainActivity.NOTIFICATION_ID, this@MainActivity.getNotification("Download canceled!", 0))
                            return@doAsync
                        }
                        else -> {
                            randomAccessFile.write(bytes, 0, bytesLength)
                            readTotal += bytesLength
                            this@MainActivity.downloadedSize = readTotal
                            
                            val progress = (readTotal * 100 / total.toDouble()).toInt()
                            this@MainActivity.notificationManager.notify(MainActivity.NOTIFICATION_ID, this@MainActivity.getNotification("Downloading", progress))
                            
                            uiThread {
                                this@MainActivity.progressDownload.progress = progress
                            }
    
                            bytesLength = stream.read(bytes)
                        }
                    }
                }
                this@MainActivity.downloadStatus = DownloadStatus.COMPLETED
                this@MainActivity.notificationManager.notify(MainActivity.NOTIFICATION_ID, this@MainActivity.getNotification("Download completed!", 0))
                uiThread {
                    this@MainActivity.labelStatus.text = "Download completed!"
                }
                response.body()!!.close()
            }
            else
            {
                uiThread {
                    this@MainActivity.labelStatus.text = "Download failed!"
                    this@MainActivity.downloadStatus = DownloadStatus.FAILED
                }
            }
        }
    }
    
    private fun pauseDownload()
    {
        if (this.downloadStatus == DownloadStatus.DOWNLOADING)
        {
            this.downloadStatus = DownloadStatus.PAUSED
            this.labelStatus.text = "Download paused!"
        }
    }
    
    private fun stopDownload()
    {
        if (this.downloadStatus == DownloadStatus.DOWNLOADING || this.downloadStatus == DownloadStatus.PAUSED)
        {
            this.downloadStatus = DownloadStatus.CANCELED
            this.labelStatus.text = "Download canceled!"
            
            this.removeDownloadedFile()
            this.progressDownload.progress = 0
        }
    }
    
    private fun removeDownloadedFile()
    {
        var removed = false
        if (this.downloadStatus != DownloadStatus.DOWNLOADING && this.downloadStatus != DownloadStatus.PAUSED)
        {
            removed = this.file.delete()
        }
        toast("File removed? $removed")
    }
    
    private fun getContentLengthAsync():Long
    {
        val request = Request.Builder().get().url(MainActivity.DOWNLOAD_URL).build()
        val response = this.client.newCall(request).execute()
        if (response != null && response.isSuccessful)
        {
            return response.body()?.contentLength() ?: 0L
        }
        return 0L
    }
}
