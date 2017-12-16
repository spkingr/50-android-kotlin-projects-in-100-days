package me.liuqingwen.android.projectfloatingwindow

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), AnkoLogger
{
    companion object
    {
        const val PREFERENCE_NAME = "project_floating_window"
        const val IS_AUTO_OPEN_FLOAT_WINDOW = "is_auto_open_float_window"
        
        const val ID_LABEL_HEADER = 0x00000001
        const val ID_BUTTON_ALERT = 0x00000002
    }
    
    private var isFloatWindowShowing = false
    private var isAutoOpenFloatWindow = true
    private val preference by lazy(LazyThreadSafetyMode.NONE) { this.getSharedPreferences(MainActivity.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    private val manager by lazy(LazyThreadSafetyMode.NONE) { this.getSystemService(Context.WINDOW_SERVICE) as WindowManager }
    private lateinit var params:WindowManager.LayoutParams
    private lateinit var view:View
    private lateinit var buttonMain:Button
    
    //new params
    private var isActionDown = false
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        
        this.isAutoOpenFloatWindow = this.preference.getBoolean(MainActivity.IS_AUTO_OPEN_FLOAT_WINDOW, true)
        
        this.initUI()
        this.init()
    }
    
    override fun onBackPressed()
    {
        //super.onBackPressed()
        this.moveTaskToBack(true)
    }
    
    private fun initUI()
    {
        relativeLayout {
            textView(R.string.text_header) {
                id = MainActivity.ID_LABEL_HEADER
            }.lparams(width = matchParent, height = wrapContent) {
                alignParentTop()
                topMargin = dip(8)
                leftMargin = dip(8)
                rightMargin = dip(8)
            }
        
            this@MainActivity.buttonMain = button(R.string.click_to_show_alert) {
                id = MainActivity.ID_BUTTON_ALERT
                setOnClickListener {
                    this@MainActivity.showFloatWindow()
                }
            }.lparams(width = matchParent, height = wrapContent) {
                below(MainActivity.ID_LABEL_HEADER)
                alignParentLeft()
                topMargin = dip(8)
                leftMargin = dip(8)
                rightMargin = dip(8)
            }
        
            checkBox(R.string.auto_open_float_window, checked = this@MainActivity.isAutoOpenFloatWindow){
                setOnClickListener {
                    this@MainActivity.isAutoOpenFloatWindow = this.isChecked
                    val editor = this@MainActivity.preference.edit()
                    editor.putBoolean(MainActivity.IS_AUTO_OPEN_FLOAT_WINDOW, this@MainActivity.isAutoOpenFloatWindow)
                    editor.apply()
                }
            }.lparams(width = wrapContent, height = wrapContent) {
                below(this@MainActivity.buttonMain)
                alignParentEnd()
                topMargin = dip(8)
                rightMargin = dip(8)
            }
        }
    }
    
    private var originX = 0f
    private var originY = 0f
    
    private fun init()
    {
        this.params = WindowManager.LayoutParams().apply {
            this.gravity = Gravity.END or Gravity.CENTER
            this.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            this.type = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            this.alpha = 0.8f
            this.dimAmount = 0.2f
            
            this.width = WindowManager.LayoutParams.WRAP_CONTENT
            this.height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        
        this.view = UI {
            verticalLayout {
                
                textView("Window") {
                    textColor = Color.WHITE
                    setOnTouchListener { _, event ->
                        when(event.action)
                        {
                            MotionEvent.ACTION_DOWN -> {
                                this@MainActivity.isActionDown = true
                                
                                this@MainActivity.originX = event.rawX
                                this@MainActivity.originY = event.rawY
                            }
                            MotionEvent.ACTION_MOVE -> {
                                if (this@MainActivity.isActionDown)
                                {
                                    val deltaX = event.rawX - this@MainActivity.originX
                                    val deltaY = event.rawY - this@MainActivity.originY
                                    this@MainActivity.params.x -= deltaX.toInt()
                                    this@MainActivity.params.y += deltaY.toInt()
    
                                    this@MainActivity.manager.updateViewLayout(this@MainActivity.view, this@MainActivity.params)
                                    this@MainActivity.originX = event.rawX
                                    this@MainActivity.originY = event.rawY
                                }
                            }
                            MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_UP -> {
                                this@MainActivity.isActionDown = false
                                
                                this@MainActivity.originX = this@MainActivity.params.x.toFloat()
                                this@MainActivity.originY = this@MainActivity.params.y.toFloat()
                            }
                            else -> { toast("Action type: ${event.action} not be recognized.") }
                        }
                        true
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    margin = dip(16)
                }
                
                textView("Move the window by touching the title.") {
                    textColor = Color.WHITE
                }.lparams(width = matchParent, height = wrapContent) {
                    margin = dip(4)
                }
                
                button("Click Me To Dismiss") {
                    setOnClickListener {
                        this@MainActivity.showFloatWindow()
                    }
                }
                
                button("Open The Main View") {
                    textColor = Color.RED
                    setOnClickListener {
                        startActivity<MainActivity>()
                    }
                }
            }
        }.view
        
        //for auto show the float window while the app is starting.
        if (this.isAutoOpenFloatWindow)
        {
            this.showFloatWindow()
        }
    }
    
    private fun showFloatWindow()
    {
        if (this.isFloatWindowShowing)
        {
            try
            {
                this.manager.removeView(this.view)
                this.isFloatWindowShowing = false
            }catch (e:Exception)
            {
                this.alertForOperations()
            }
        }
        else
        {
            try
            {
                this.manager.addView(this.view, this.params)
                this.isFloatWindowShowing = true
            }
            catch (e:Exception)
            {
                this.alertForOperations()
            }
        }
        
        this.buttonMain.textResource = if (this@MainActivity.isFloatWindowShowing) R.string.click_to_hide_alert else R.string.click_to_show_alert
    }
    
    private fun alertForOperations()
    {
        alert {
            title = "Error!"
            message = "Error while open(close) the float window, make sure you have let it grant the right permissions, and try it again."
            negativeButton("Done") { it.dismiss() }
        }.show()
    }
}
