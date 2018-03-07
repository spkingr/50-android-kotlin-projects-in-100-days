package me.liuqingwen.android.projectobjectbox

import android.support.v4.app.Fragment
import com.bumptech.glide.request.RequestOptions

/**
 * Created by Qingwen on 2018-3-2, project: ProjectObjectBox.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-2
 * @Package: me.liuqingwen.android.projectobjectbox.view in project: ProjectObjectBox
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

abstract class BasicFragment:Fragment()
{
    private val requestOptions by lazy { RequestOptions() }
    protected fun getGlideRequest(builder: RequestOptions.()->Unit) = (this.context?.applicationContext as? MyApplication)?.glideManager?.applyDefaultRequestOptions(this.requestOptions.apply { this.builder() })?.asDrawable()
}