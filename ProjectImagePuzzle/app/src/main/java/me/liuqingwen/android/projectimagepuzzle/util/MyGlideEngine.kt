package me.liuqingwen.android.projectimagepuzzle.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.zhihu.matisse.engine.ImageEngine

/**
 * Created by Qingwen on 2018-6-17, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-6-17
 * @Package: me.liuqingwen.android.projectandroidtest.util in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class MyGlideEngine : ImageEngine{
    override fun loadImage(context: Context?, resizeX: Int, resizeY: Int, imageView: ImageView?, uri: Uri?)
    {
        Glide.with(context!!).load(uri).apply(RequestOptions().apply {
            this.override(resizeX, resizeY)
            this.centerCrop()
            this.priority(Priority.HIGH)
        }).into(imageView!!)
    }
    
    override fun loadGifImage(context: Context?, resizeX: Int, resizeY: Int, imageView: ImageView?, uri: Uri?)
    {
        Glide.with(context!!).asGif().load(uri).apply(RequestOptions().apply {
            this.override(resizeX, resizeY)
            this.centerCrop()
            this.priority(Priority.HIGH)
        }).into(imageView!!)
    }
    
    override fun supportAnimatedGif() = true
    
    override fun loadThumbnail(context: Context?, resize: Int, placeholder: Drawable?, imageView: ImageView?, uri: Uri?)
    {
        Glide.with(context!!).asBitmap().load(uri).apply(RequestOptions().apply {
            this.override(resize, resize)
            this.centerCrop()
            this.placeholder(placeholder)
        }).into(imageView!!)
    }
    
    override fun loadGifThumbnail(context: Context?, resize: Int, placeholder: Drawable?, imageView: ImageView?, uri: Uri?) =
            this.loadThumbnail(context, resize, placeholder, imageView, uri)
}