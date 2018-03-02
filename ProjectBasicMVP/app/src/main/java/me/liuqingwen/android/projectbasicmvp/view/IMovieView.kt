package me.liuqingwen.android.projectbasicmvp.view

import me.liuqingwen.android.projectbasicmvp.model.Movie

/**
 * Created by Qingwen on 2018-2-16, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-16
 * @Package: me.liuqingwen.android.projectbasicmvp.view in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

interface IMovieView
{
    fun onLoadStarted()
    fun onLoadError(message: String?)
    fun onLoadSuccess(it: List<Movie>)
}