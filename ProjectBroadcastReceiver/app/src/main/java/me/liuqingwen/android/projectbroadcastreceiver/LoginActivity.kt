package me.liuqingwen.android.projectbroadcastreceiver

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.layout_activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity()
{
    companion object
    {
        fun getIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
    
    private val connectivityManager by lazy(LazyThreadSafetyMode.NONE) { this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_login)
        
        this.init()
    }
    
    private fun init()
    {
        this.setupLogoutButton()
        
        this.buttonReset.setOnClickListener {
            this.textUsername.text.clear()
            this.textPassword.text.clear()
        }
        this.buttonLogin.setOnClickListener {
            val username = this.textUsername.text.toString()
            val password = this.textPassword.text.toString()
            if (username == "admin" && password == "123456")
            {
                val netInfo = this.connectivityManager.activeNetworkInfo
                if (netInfo == null || ! netInfo.isAvailable)
                {
                    toast("Network is unavailable!")
                }
                else
                {
                    IS_LOG_IN = true
                    
                    val intent = MainActivity.getIntent(this)
                    this.startActivity(intent)
                    
                    this.finish()
                }
            }
        }
    }
}
