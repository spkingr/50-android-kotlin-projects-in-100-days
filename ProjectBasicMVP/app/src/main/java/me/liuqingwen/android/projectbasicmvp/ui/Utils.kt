package me.liuqingwen.android.projectbasicmvp.ui

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.inputmethod.InputMethodManager

/**
 * Created by Qingwen on 2018-3-1, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-1
 * @Package: me.liuqingwen.android.projectbasicmvp.ui in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

fun RecyclerView.fastSmoothScrollToPosition(currentPosition: Int, targetPosition: Int = 0, threshold: Int = 4)
{
    if ((currentPosition - targetPosition) * (currentPosition - targetPosition) > threshold * threshold)
    {
        this.scrollToPosition(if (currentPosition > targetPosition) threshold + targetPosition else targetPosition - threshold)
    }
    this.smoothScrollToPosition(targetPosition)
}

fun Activity.hideKeyboard()
{
    this.currentFocus?.let {
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}
fun Fragment.hideKeyboard()
{
    this.activity?.currentFocus?.let {
        val inputManager = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputManager?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}