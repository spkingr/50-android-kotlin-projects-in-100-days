package me.liuqingwen.android.projectimagepuzzle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import java.io.InputStream
import kotlin.math.min

/**
 * Created by Qingwen on 2018-2018-8-6, project: ProjectImagePuzzle.
 *
 * @Author: Qingwen
 * @DateTime: 2018-8-6
 * @Package: me.liuqingwen.android.projectimagepuzzle in project: ProjectImagePuzzle
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

fun sampleBitmapData(destWidth: Int, destHeight: Int, stream: InputStream): Bitmap?
{
    if (destWidth <= 0 || destHeight <= 0)
    {
        return null
    }
    
    return stream.use {
        val option = BitmapFactory.Options().apply { this.inJustDecodeBounds = true }
        BitmapFactory.decodeStream(it, Rect(-1, -1, -1, -1), option)
        val srcWidth = option.outWidth
        val srcHeight = option.outHeight
        val ratio = min(srcWidth / destWidth, srcHeight / destHeight)
    
        it.reset()
        with(option){
            this.inJustDecodeBounds = false
            this.inSampleSize = ratio
        }
        BitmapFactory.decodeStream(it, Rect(-1, -1, -1, -1), option)
    }
}