package me.liuqingwen.android.projectobjectbox

import android.content.Context
import android.view.View
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.NonCancellable

/**
 * Created by Qingwen on 2018-3-4, project: ProjectObjectBox.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-4
 * @Package: me.liuqingwen.android.projectobjectbox.contract in project: ProjectObjectBox
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

interface IJobHolder
{
    val job: Job
}

val View.contextJob : Job get() = (this.context as? IJobHolder)?.job ?: NonCancellable
val Context?.contextJob : Job get() = (this as? IJobHolder)?.job ?: NonCancellable