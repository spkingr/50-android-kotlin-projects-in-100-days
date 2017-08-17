package me.liuqingwen.android.projectasyncworks

import android.graphics.Paint
import android.net.Uri
import android.os.*
import android.support.v4.os.EnvironmentCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.layout_activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity()
{
    
    private val client by lazy { OkHttpClient.Builder().build() }
    private var isRunning = false
    private val myHandler = MyHandler()
    private var downloadLink:String? = null
    private var downloadTask = DownloadTask()
    private var isDownloadSuccess = false
    
    private val buttonClickHandler = { view:View ->
        if (! this.downloadLink.isNullOrBlank())
        {
            when(view.id)
            {
                R.id.buttonStart -> {
                    if (this.downloadTask.isCanceled)
                    {
                        this.downloadTask = DownloadTask()
                        this.downloadTask.execute(this.downloadLink!!)
                    } else if (! this.isDownloadSuccess)
                    {
                        this.downloadTask.execute(this.downloadLink!!)
                    }
                }
                R.id.buttonCancel -> {
                    if (! this.isDownloadSuccess)
                    {
                        this.downloadTask.isCanceled = true
                    }
                }
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
        this.labelUrlAddress.paintFlags = Paint.UNDERLINE_TEXT_FLAG or this.labelUrlAddress.paintFlags
        this.labelFileUrl.paintFlags = Paint.UNDERLINE_TEXT_FLAG or this.labelFileUrl.paintFlags
        
        this.buttonGetFileUrl.setOnClickListener {
            if (! this.isRunning)
            {
                this.isRunning = true
                thread(start = true, isDaemon = true) {
                    val request = Request.Builder().get().url("http://liuqingwen.me/data/get-images-json.php?type=text").build()
                    val response = this.client.newCall(request).execute()
                    if (response.isSuccessful)
                    {
                        val body = response.body()?.string()
                        val message = this.myHandler.obtainMessage()
                        message.what = 101
                        message.obj = body
                        this.myHandler.sendMessage(message)
                    }
                    response.close()
                    this.isRunning = false
                }
            }
        }
        
        this.buttonStart.setOnClickListener(this.buttonClickHandler)
        this.buttonCancel.setOnClickListener(this.buttonClickHandler)
    }
    
    private fun toggleDownload()
    {
        this.downloadLink?.let {
            this.labelFileUrl.text = it
            this.buttonStart.isEnabled = true
        }
    }
    
    inner class MyHandler : Handler()
    {
        override fun handleMessage(msg: Message?)
        {
            super.handleMessage(msg)
            if (msg?.what == 101)
            {
                this@MainActivity.downloadLink = msg.obj as? String
                this@MainActivity.toggleDownload()
            }
        }
    }
    
    inner class DownloadTask:AsyncTask<String, Int, DownloadResult>()
    {
        var isCanceled = false
        private var lastDownloadedSize = 0L
        private var fileName = "download_temp_file.apk"
        
        override fun onPreExecute()
        {
            super.onPreExecute()
            this@MainActivity.progressDownloadBar.progress = 0
            this@MainActivity.progressDownloadBar.visibility = View.VISIBLE
            this@MainActivity.buttonCancel.isEnabled = true
        }
    
        override fun doInBackground(vararg params: String?): DownloadResult
        {
            val urlString = params[0]!!
            val request = Request.Builder().url(urlString).build()
            val response = this@MainActivity.client.newCall(request).execute()
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + this.fileName)
            this.lastDownloadedSize = if (file.exists()) file.length() else file.let { it.createNewFile(); 0L }
            if (response != null && response.isSuccessful)
            {
                val body = response.body()
                if (body == null)
                {
                    return DownloadResult.FAILED
                }
                else
                {
                    val totalFileSize = body.contentLength()
                    if (this.lastDownloadedSize >= totalFileSize)
                    {
                        return DownloadResult.SUCCESS
                    }
                    else
                    {
                        val bytes = ByteArray(1024)
                        val stream = body.byteStream()
                        var length = stream.read(bytes)
                        val outputFile = RandomAccessFile(file, "rw")
                        outputFile.seek(this.lastDownloadedSize)
                        while (length != -1)
                        {
                            outputFile.write(bytes, 0, length)
                            this.lastDownloadedSize += length
                            val progress = (this.lastDownloadedSize * 100L / totalFileSize).toInt()
                            this.publishProgress(progress)
                            
                            if (this.isCanceled)
                            {
                                file.delete()
                                return DownloadResult.CANCELED
                            }
                            
                            length = stream.read(bytes)
                        }
                        return DownloadResult.SUCCESS
                    }
                }
            }
            else
            {
                return DownloadResult.FAILED
            }
        }
    
        override fun onProgressUpdate(vararg values: Int?)
        {
            super.onProgressUpdate(*values)
            if(this@MainActivity.progressDownloadBar.visibility == View.INVISIBLE)
            {
                this@MainActivity.progressDownloadBar.visibility = View.VISIBLE
            }
            this@MainActivity.progressDownloadBar.progress = values[0]!!
        }
    
        override fun onPostExecute(result: DownloadResult)
        {
            super.onPostExecute(result)
            if (result == DownloadResult.SUCCESS)
            {
                this@MainActivity.isDownloadSuccess = true
                this@MainActivity.progressDownloadBar.visibility = View.INVISIBLE
                this@MainActivity.toast("Download successfully!")
            }
            else if(result == DownloadResult.CANCELED)
            {
                this@MainActivity.toast("Download Canceled!")
            }else
            {
                this@MainActivity.toast("Download failed!")
            }
        }
    
        override fun onCancelled()
        {
            super.onCancelled()
            this@MainActivity.toast("User canceled!")
        }
    }
}

enum class DownloadResult
{
    SUCCESS, CANCELED, FAILED
}
