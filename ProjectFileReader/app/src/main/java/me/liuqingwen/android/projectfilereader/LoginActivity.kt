package me.liuqingwen.android.projectfilereader

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.layout_activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity()
{
    companion object
    {
        const val PREFERENCES_NAME = "username_password"
        const val USER_NAME = "username"
        const val USER_PASS = "password"
    }
    
    private val preferences by lazy { this.getSharedPreferences(LoginActivity.PREFERENCES_NAME, Context.MODE_PRIVATE) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_login)
        
        this.readData()
        this.init()
    }
    
    private fun readData()
    {
        val username = this.preferences.getString(LoginActivity.USER_NAME, "")
        val password = this.preferences.getString(LoginActivity.USER_PASS, "")
        this.textUsername.setText(username)
        this.textPassword.setText(password)
        
        if (username.isNotBlank())
        {
            this.checkboxRemeber.isChecked = true
        }
    }
    
    private fun init()
    {
        this.buttonReset.setOnClickListener {
            this.textUsername.text.clear()
            this.textPassword.text.clear()
        }
        this.buttonLogin.setOnClickListener {
            val userName = this.textUsername.text.toString()
            val password = this.textPassword.text.toString()
            if (userName == "admin" && password == "123456")
            {
                val edit = this.preferences.edit()
                edit.putString(LoginActivity.USER_NAME, if(this.checkboxRemeber.isChecked) userName else "" )
                edit.putString(LoginActivity.USER_PASS, if(this.checkboxRemeber.isChecked) password else "" )
                edit.apply()
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
            }
            else
            {
                toast("User name or password is incorrect!")
            }
        }
    }
}
