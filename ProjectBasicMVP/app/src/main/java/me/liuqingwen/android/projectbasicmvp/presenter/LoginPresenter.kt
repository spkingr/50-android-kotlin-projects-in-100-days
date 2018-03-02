package me.liuqingwen.android.projectbasicmvp.presenter

import io.reactivex.disposables.CompositeDisposable
import me.liuqingwen.android.projectbasicmvp.R
import me.liuqingwen.android.projectbasicmvp.model.APILoginService
import me.liuqingwen.android.projectbasicmvp.view.ILoginView
import org.jetbrains.anko.AnkoLogger

/**
 * Created by Qingwen on 2018-2-12, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-12
 * @Package: me.liuqingwen.android.projectbasicmvp.presenter in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class LoginPresenter(private val view: ILoginView): AnkoLogger
{
    private val disposables = CompositeDisposable()
    
    private fun tryLogin()
    {
        val usernameErrorId = this.findUsernameErrorId(this.view.username)
        if (usernameErrorId != null)
        {
            this.view.textUsernameRequestFocus()
            this.view.showHintByToast(usernameErrorId)
            return
        }
        val passwordErrorId = this.findPasswordErrorId(this.view.password)
        if (passwordErrorId != null)
        {
            this.view.textPasswordRequestFocus()
            this.view.showHintByToast(passwordErrorId)
            return
        }
        
        this.view.isProgressBarVisible = true
        this.disposables.add(APILoginService.login(this.view.username, this.view.password, {
            this.view.doLoginSuccess()
        }, {
            this.view.isProgressBarVisible = false
            this.view.doLoginError(it)
        }))
    }
    
    private fun findUsernameErrorId(username:String) = when {
        username.length < 5                         -> R.string.username_length_error
        username.contains(' ')                 -> R.string.username_space_error
        username.first().toLowerCase() !in 'a'..'z' -> R.string.username_start_error
        else                                        -> null
    }
    
    private fun findPasswordErrorId(password:String) = when {
        password.length < 6                         -> R.string.password_length_error
        else                                        -> null
    }
    
    fun onCreate()
    {
        this.view.isProgressBarVisible = false
        
        this.view.onLoginButtonClickAction = {
            this.tryLogin()
        }
        
        this.view.onPasswordEnterAction = {
            this.tryLogin()
        }
    }
    
    fun onDestroy()
    {
        this.disposables.dispose()
    }
}