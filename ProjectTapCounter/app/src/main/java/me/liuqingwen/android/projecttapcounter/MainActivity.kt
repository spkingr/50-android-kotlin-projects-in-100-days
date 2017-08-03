package me.liuqingwen.android.projecttapcounter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity()
{
    private var count = 0
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.buttonTap.setOnClickListener {
            this.count ++
            this.labelNumber.text = this.count.toString()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        this.menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            R.id.menuReset -> {
                this.count = 0
                this.labelNumber.text = "0"
            }
            else -> this.toast("Error while item selected!")
        }
        return super.onOptionsItemSelected(item)
    }
}
