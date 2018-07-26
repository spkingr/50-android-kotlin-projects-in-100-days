package me.liuqingwen.android.projectandroidtest

import android.graphics.Color
import android.view.View

/**
 * Created by Qingwen on 2018-6-9, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-6-9
 * @Package: me.liuqingwen.android.projectandroidtest in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

enum class FloatingButtonType(val srcCompat: Int, val backgroundTint: Int)
{
    ADD(R.drawable.ic_add_black_24dp, R.color.colorAccent),
    DONE(R.drawable.ic_check_black_24dp, R.color.blueColor),
    CANCEL(R.drawable.ic_clear_black_24dp, R.color.redColor),
    NONE(0, 0)
}

interface IMainInteractionListener
{
    fun setTitle(title : String)
    fun configFloatingButton(buttonType: FloatingButtonType, isVisible:Boolean,  onClickListener: ((View) -> Unit)?)
}