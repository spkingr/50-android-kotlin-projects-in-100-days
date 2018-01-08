package me.liuqingwen.android.projectbetterpracticefragment

import android.text.SpannableStringBuilder

/**
 * Created by Qingwen on 2018-2018-1-2, project: ProjectBetterPracticeFragment.
 *
 * @Author: Qingwen
 * @DateTime: 2018-1-2
 * @Package: me.liuqingwen.android.projectbetterpracticefragment in project: ProjectBetterPracticeFragment
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

fun String?.toEditable() = SpannableStringBuilder(this ?: "")
