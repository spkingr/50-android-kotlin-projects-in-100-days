package me.liuqingwen.android.projectviewpager

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.layout_activity_app.*
import org.jetbrains.anko.toast

class AppActivity : AppCompatActivity()
{
    private val preference by lazy(LazyThreadSafetyMode.NONE) { this.getSharedPreferences(MainActivity.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_app)
        
        this.init()
    }
    
    private fun init()
    {
        this.buttonClear.setOnClickListener{
            val editor = this.preference.edit()
            editor.clear()
            val result = editor.commit()
            
            if (result)
            {
                toast("Done!")
            }
        }
    }
}
