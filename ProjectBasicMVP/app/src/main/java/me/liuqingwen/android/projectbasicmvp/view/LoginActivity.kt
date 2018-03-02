package me.liuqingwen.android.projectbasicmvp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import me.liuqingwen.android.projectbasicmvp.presenter.LoginPresenter
import me.liuqingwen.android.projectbasicmvp.ui.*
import me.liuqingwen.kotlinandroidviewbindings.*
import org.jetbrains.anko.*

class LoginActivity : AppCompatActivity(), ILoginView
{
    override var isProgressBarVisible by bindToLoadings(ID_PROGRESS_BAR, ID_BUTTON_LOGIN)
    override var username by bindToEditText(ID_TEXT_USERNAME)
    override var password by bindToEditText(ID_TEXT_PASSWORD)
    override val textUsernameRequestFocus by bindToRequestFocus(ID_TEXT_USERNAME)
    override val textPasswordRequestFocus by bindToRequestFocus(ID_TEXT_PASSWORD)
    override var usernameErrorId by bindToErrorId(ID_TEXT_USERNAME, this)
    override var passwordErrorId:Int? by bindToErrorId(ID_TEXT_PASSWORD, this)
    override var onPasswordEnterAction by bindToEditorActions(ID_TEXT_PASSWORD) {actionId, eventCode ->
        actionId == EditorInfo.IME_ACTION_DONE || eventCode == KeyEvent.KEYCODE_ENTER
    }
    override var onLoginButtonClickAction by bindToClickEvent(ID_BUTTON_LOGIN)
    
    private val presenter by lazy(LazyThreadSafetyMode.NONE) { LoginPresenter(this) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        LoginUI().setContentView(this)
        
        this.init()
        this.presenter.onCreate()
    }
    
    private fun init()
    {
        val labelUsername = this.find<TextView>(ID_LABEL_USERNAME)
        val labelPassword = this.find<TextView>(ID_LABEL_PASSWORD)
        val textUsername = this.find<EditText>(ID_TEXT_USERNAME)
        val textPassword = this.find<EditText>(ID_TEXT_PASSWORD)
        textUsername.setOnFocusChangeListener { _, isFocused ->
            if (this.username.isEmpty())
            {
                if(isFocused) labelUsername.animate().setDuration(300).translationY(-68f).scaleX(0.8f).scaleY(0.8f).start() else labelUsername.animate().setDuration(300).translationY(0f).scaleX(1.0f).scaleY(1.0f).start()
            }
        }
        textPassword.setOnFocusChangeListener { _, isFocused ->
            if (this.password.isEmpty())
            {
                if(isFocused) labelPassword.animate().setDuration(300).translationY(-68f).scaleX(0.8f).scaleY(0.8f).start() else labelPassword.animate().setDuration(300).translationY(0f).scaleX(1.0f).scaleY(1.0f).start()
            }
        }
    }
    
    override fun onDestroy()
    {
        super.onDestroy()
        this.presenter.onDestroy()
    }
    
    override fun doLoginSuccess()
    {
        this.toast("Login succeed!")
        
        this.startActivity<MovieActivity>()
        this.overridePendingTransition(me.liuqingwen.android.projectbasicmvp.R.anim.anim_enter, me.liuqingwen.android.projectbasicmvp.R.anim.anim_exit)
        this.finish()
    }
    
    @SuppressLint("ResourceType")
    override fun showHintByToast(@IdRes id:Int)
    {
        this.longToast(this.getString(id))
    }
    
    override fun doLoginError(error:Throwable)
    {
        alert {
            title = "Error!"
            message = "Error while login, please try it later, information: ${error.message}"
            negativeButton("Cancel") {  }
        }.show()
    }
}
