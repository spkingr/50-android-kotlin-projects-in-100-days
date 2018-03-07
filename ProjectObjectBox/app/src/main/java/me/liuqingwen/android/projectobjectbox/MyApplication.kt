package me.liuqingwen.android.projectobjectbox

import android.app.Application
import com.bumptech.glide.Glide
import io.objectbox.BoxStore
import me.liuqingwen.android.projectobjectbox.MyObjectBox

/**
 * Created by Qingwen on 2018-3-2, project: ProjectObjectBox.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-2
 * @Package: me.liuqingwen.android.projectobjectbox.view in project: ProjectObjectBox
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class MyApplication:Application()
{
    val objectBoxStore by lazy { MyObjectBox.builder().androidContext(this).build()!! }
    val glideManager by lazy(LazyThreadSafetyMode.NONE) { Glide.with(this) }
}