package me.liuqingwen.android.projectparcelabledata

import android.text.SpannableStringBuilder

/**
 * Created by Qingwen on 2017-2017-12-23, project: ProjectParcelableData.
 *
 * @Author: Qingwen
 * @DateTime: 2017-12-23
 * @Package: me.liuqingwen.android.projectparcelabledata in project: ProjectParcelableData
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

fun String?.toEditable() = SpannableStringBuilder(this ?: "")