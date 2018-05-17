package me.liuqingwen.android.projectservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.notificationManager
import java.util.*
import kotlin.concurrent.timer

class MyService : Service(), AnkoLogger
{
    companion object
    {
        private const val JOB_NAME = "douban"
        private const val JOB_PERIOD = 60 * 60 * 1000L
        
        private const val NOTIFICATION_CHANNEL_ID = "1"
        private const val NOTIFICATION_CHANNEL_NAME = "Notification_douban"
        private const val NOTIFICATION_ID = 0
    }
    
    override fun onBind(intent: Intent): IBinder
    {
        return MyBinder();
    }
    
    inner class MyBinder(var refreshListener : ((Boolean) -> Unit)? = null): Binder()
    {
        private var isWorking = false
        private var disposable:Disposable? = null
        private var start = 0
        private val count = 100
        
        fun startFetchData()
        {
            if (this.isWorking)
            {
                return
            }
            
            this.refreshListener?.invoke(true)
            
            this.isWorking = true
            val date = Date()
            timer(name = MyService.JOB_NAME, daemon = true, startAt = date, period = MyService.JOB_PERIOD){
                if(this@MyBinder.disposable?.isDisposed == false)
                {
                    this@MyBinder.disposable?.dispose()
                }
                
                this@MyBinder.disposable = APIService.getComingSoonObservable(start = this@MyBinder.start, count = this@MyBinder.count)
                        //.subscribeOn(Schedulers.io()) //Timer is not the main thread, so no need switch subscriber thread
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { this@MyBinder.refreshListener?.invoke(true) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete{ this@MyBinder.refreshListener?.invoke(false) }
                        .subscribe {
                            val movies = it.subjects
                            if (movies.isNotEmpty())
                            {
                                this@MyBinder.start += movies.size
                                this@MyBinder.updateCache(movies)
                                this@MyBinder.notifyUpdated()
                            }
                        }
            }
        }
        
        private fun updateCache(movies: List<Movie>)
        {
            GlobalCache.addMovies(movies)
        }
        
        private fun notifyUpdated()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                val channel = NotificationChannel(MyService.NOTIFICATION_CHANNEL_ID, MyService.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                this@MyService.notificationManager.createNotificationChannel(channel)
            }
            
            val notification = NotificationCompat.Builder(this@MyService, MyService.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Update")
                    .setSmallIcon(R.drawable.ic_voice_chat_black_96dp)
                    .setContentText("Movie list has updated successfully!")
                    .setDefaults(NotificationCompat.FLAG_AUTO_CANCEL)
                    .setAutoCancel(true)
                    .build()
            
            this@MyService.notificationManager.notify(MyService.NOTIFICATION_ID, notification)
        }
        
    }
}
