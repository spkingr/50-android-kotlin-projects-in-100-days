package me.liuqingwen.android.projectbasicmvp.view

import android.support.annotation.IdRes

/**
 * Created by Qingwen on 2018-2-12, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-12
 * @Package: me.liuqingwen.android.projectbasicmvp.view in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

interface ILoginView
{
    var isProgressBarVisible:Boolean
    
    var username:String
    var password:String
    val textUsernameRequestFocus: () -> Unit
    val textPasswordRequestFocus: () -> Unit
    var usernameErrorId:Int?
    var passwordErrorId:Int?
    
    var onPasswordEnterAction: () -> Unit
    var onLoginButtonClickAction: () -> Unit
    
    fun doLoginSuccess()
    fun doLoginError(error:Throwable)
    fun showHintByToast(@IdRes id:Int)
}