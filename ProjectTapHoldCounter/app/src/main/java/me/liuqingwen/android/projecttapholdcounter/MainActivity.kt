package me.liuqingwen.android.projecttapholdcounter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), AnkoLogger
{
    
    private var count by Delegates.observable(0) { _, _, new ->
        this.labelNumber.text = new.toString()
    }
    private var timer:Timer? = null
    private var countTask:TimerTask? = null
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.buttonTap.setOnLongClickListener {
            if(this.timer == null || this.countTask == null)
            {
                this.timer = Timer()
                this.countTask = timerTask {
                    runOnUiThread {
                        this@MainActivity.count ++
                    }
                }
            }
            this.timer!!.schedule(this.countTask, 0, 100)
            true
        }
        
        this.buttonTap.setOnTouchListener { _, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_UP)
            {
                this.timer?.cancel()
                this.timer = null
                this.countTask?.cancel()
                this.countTask = null
            }
            false
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
            R.id.menuReset -> this.count = 0
            else -> this.toast("Error while item selected!")
        }
        return super.onOptionsItemSelected(item)
    }
}
