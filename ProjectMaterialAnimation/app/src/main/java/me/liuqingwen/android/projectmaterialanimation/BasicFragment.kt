package me.liuqingwen.android.projectmaterialanimation

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * Created by Qingwen on 2018-3-11, project: ProjectMaterialAnimation.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-11
 * @Package: me.liuqingwen.android.projectmaterialanimation in project: ProjectMaterialAnimation
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

@SuppressLint("CheckResult")
abstract class BasicFragment:Fragment()
{
    protected val glideManager by lazy(LazyThreadSafetyMode.NONE) { Glide.with(this) }
    protected val glideRequest by lazy(LazyThreadSafetyMode.NONE) { this.glideManager.applyDefaultRequestOptions( RequestOptions().apply {
            this.placeholder(R.drawable.placeholder)
            this.error(R.drawable.image_load_error)
            this.fitCenter()
        } ).asDrawable()
    }
}

/*
interface IJobHolder
{
    val job:Job
}

val View.contextJob get() = (this.context as? IJobHolder)?.job ?: NonCancellable
*/
