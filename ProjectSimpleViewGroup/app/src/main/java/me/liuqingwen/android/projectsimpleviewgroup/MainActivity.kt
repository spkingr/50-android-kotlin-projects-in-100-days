package me.liuqingwen.android.projectsimpleviewgroup

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity()
{
    private var count = 0
    private var message by Delegates.observable("") {_, _, new->
        this.textMessage.text = "[${++ this.count}]: $new"
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.simpleViewGroup.setOnClickListener {
            this.message = "You clicked me!"
        }
        this.simpleViewGroup.setOnLongClickListener {
            this.message = "You long clicked me!"
            true
        }
        this.button1.setOnClickListener {
            this.message = "You clicked button1"
        }
        this.button2.setOnClickListener {
            this.message = "Another Button"
        }
    }
}
