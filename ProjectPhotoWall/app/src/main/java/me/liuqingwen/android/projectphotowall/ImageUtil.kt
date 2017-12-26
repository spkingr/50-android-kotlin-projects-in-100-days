package me.liuqingwen.android.projectphotowall

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.text.SpannableStringBuilder
import android.util.LruCache
import android.view.WindowManager
import android.widget.ImageView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

/**
 * Created by Qingwen on 2017-2017-12-24, project: ProjectPhotoWall.
 *
 * @Author: Qingwen
 * @DateTime: 2017-12-24
 * @Package: me.liuqingwen.android.projectphotowall in project: ProjectPhotoWall
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

fun String?.toEditable() = SpannableStringBuilder(this ?: "")

val cacheSize = (Runtime.getRuntime().maxMemory() / 1024 / 8).toInt()
val lruCache = object : LruCache<String, Bitmap>(cacheSize){
    override fun sizeOf(key: String?, value: Bitmap?): Int
    {
        return value!!.byteCount / 1024
    }
}

fun getImageFromCache(key:String):Bitmap? = lruCache[key]

fun saveImageCache(key:String, value:Bitmap)
{
    if (lruCache[key] == null)
    {
        lruCache.put(key, value)
    }
}

fun displayImageFromUrl(context: Context, url:String, imageView: ImageView)
{
    var bitmap = getImageFromCache(url)
    if (bitmap != null)
    {
        imageView.setImageBitmap(bitmap)
    }
    else
    {
        imageView.setImageResource(R.drawable.placeholder)
        context.doAsync{
            bitmap = try
            {
                val httpUrl = URL(url)
                val connection = httpUrl.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 8000
                connection.readTimeout = 16000
                val stream = connection.inputStream
                BitmapFactory.decodeStream(stream)
            }
            catch (e:Exception)
            {
                null
            }
            
            uiThread {
                if (bitmap == null)
                {
                    imageView.setImageResource(R.drawable.image_load_error)
                }
                else
                {
                    val data = bitmap!!
                    imageView.setImageBitmap(bitmap)
                    saveImageCache(url, data)
                }
            }
        }
    }
}

fun isBigImage(context: Context, stream: InputStream):Pair<Boolean, Int>
{
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val size = Point()
    windowManager.defaultDisplay.getSize(size)
    val maxScreenSize = if (size.x > size.y) size.x else size.y
    
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeStream(stream, Rect(), options)
    if (options.outWidth > maxScreenSize || options.outHeight > maxScreenSize)
    {
        val heightRatio = (options.outHeight / maxScreenSize.toFloat()).roundToInt()
        val widthRatio = (options.outWidth / maxScreenSize.toFloat()).roundToInt()
        val ratio = if(heightRatio > widthRatio) heightRatio else widthRatio
        return Pair(true, ratio)
    }
    return Pair(false, 1)
}

fun loadImage(context: Context, stream: InputStream):Bitmap
{
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val size = Point()
    windowManager.defaultDisplay.getSize(size)
    val maxImageSize = if (size.x > size.y) size.x else size.y
    
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    val bitmap = BitmapFactory.decodeStream(stream, Rect(), options)
    if (options.outWidth > maxImageSize || options.outHeight > maxImageSize)
    {
        val heightRatio = (options.outHeight / maxImageSize.toFloat()).roundToInt()
        val widthRatio = (options.outWidth / maxImageSize.toFloat()).roundToInt()
        val ratio = if(heightRatio > widthRatio) heightRatio else widthRatio
        options.inSampleSize = ratio
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeStream(stream, Rect(), options)
    }
    return bitmap
}