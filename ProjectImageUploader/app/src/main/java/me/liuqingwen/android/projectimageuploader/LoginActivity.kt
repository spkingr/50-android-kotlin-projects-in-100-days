package me.liuqingwen.android.projectimageuploader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.AnimationUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_activity_login.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.*

class LoginActivity : AppCompatActivity(), AnkoLogger
{
    companion object
    {
        private const val FORM_USERNAME = "username"
        private const val FORM_PASSWORD = "password"
        private const val API_LOGIN_URL = "http://liuqingwen.me/test/login_db.php"
    }
    private val httpClient by lazy(LazyThreadSafetyMode.NONE) { OkHttpClient() }
    private var cookie:String? = null
    private val username:String
        get()
        {
            return this.textUsername.text.toString()
        }
    private val password:String
        get()
        {
            return this.textPassword.text.toString()
        }
    private val animationShow by lazy(LazyThreadSafetyMode.NONE) { AnimationUtils.loadAnimation(this, R.anim.anim_label_show) }
    private val animationShow2 by lazy(LazyThreadSafetyMode.NONE) { AnimationUtils.loadAnimation(this, R.anim.anim_label_show_2) }
    private val animationHide by lazy(LazyThreadSafetyMode.NONE) { AnimationUtils.loadAnimation(this, R.anim.anim_label_hide) }
    private val animationHide2 by lazy(LazyThreadSafetyMode.NONE) { AnimationUtils.loadAnimation(this, R.anim.anim_label_hide_2) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_login)
        
        this.init()
    }
    
    private fun init()
    {
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.hide()
    
        this.buttonLogin.requestFocus()
        
        this.buttonLogin.setOnClickListener {
            if (this.username.isBlank() || this.password.isBlank())
            {
                this.alert {
                    this.title = "Info"
                    this.message = "Neither username or password cannot be empty!"
                    negativeButton("OK") {  }
                }.show()
            }
            else
            {
                this.buttonLogin.isEnabled = false
                val formBody = FormBody.Builder()
                        .add(LoginActivity.FORM_USERNAME, this.username)
                        .add(LoginActivity.FORM_PASSWORD, this.password)
                        .build()
                val request = Request.Builder().post(formBody).url(LoginActivity.API_LOGIN_URL).build()
                this.doAsync {
                    val body = try
                    {
                        val response = this@LoginActivity.httpClient.newCall(request).execute()
                        this@LoginActivity.cookie = response.header("Set-Cookie")
                        if (response.isSuccessful) response.body()?.string() else null
                    }catch (e:Exception)
                    {
                        null
                    }
                    
                    uiThread {
                        if (body == null)
                        {
                            this@LoginActivity.alert {
                                this.title = "Login Failed"
                                this.message = "Make sure your network is online, and retry again."
                                positiveButton("OK") {  }
                            }.show()
                            this@LoginActivity.buttonLogin.isEnabled = true
                        }
                        else
                        {
                            val info = Gson().fromJson<LoginResult>(body, LoginResult::class.java)
                            if (info.result)
                            {
                                this@LoginActivity.buttonLogin.isEnabled = true
                                this@LoginActivity.startActivity<MainActivity>(MainActivity.COOKIE_EXTRA to (this@LoginActivity.cookie ?: ""))
                            }
                            else
                            {
                                this@LoginActivity.alert {
                                    this.title = "Login Failed"
                                    this.message = info.info
                                    positiveButton("Cancel") {  }
                                }.show()
                                this@LoginActivity.buttonLogin.isEnabled = true
                            }
                        }
                    }
                }
            }
        }
        
        this.textUsername.setOnFocusChangeListener { _, isFocused ->
            if (this.username.isEmpty())
            {
                this.labelUsername.startAnimation(if (isFocused) this.animationShow else this.animationHide)
            }
        }
        
        this.textPassword.setOnFocusChangeListener { _, isFocused ->
            if (this.password.isEmpty())
            {
                this.labelPassword.startAnimation(if (isFocused) this.animationShow2 else this.animationHide2)
            }
        }
    }
}
