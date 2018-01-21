package me.liuqingwen.android.projectmyswitch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_activity_main.*

class MainActivity : AppCompatActivity()
{
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.switchDiabled.isEnabled = false
        
        this.switchDefault.setOnSwitchValueChangedListener { _, isOn ->
            val text = if (isOn) "Default: On" else "Default: Off"
            this.labelDefault.text = text
        }
        this.switchAnimation.setOnSwitchValueChangedListener { _, isOn ->
            val text = if (isOn) "Animation(Slow): On" else "Animation(Slow): Off"
            this.labelAnimation.text = text
        }
        this.switchPadding.setOnSwitchValueChangedListener { _, isOn ->
            val text = if (isOn) "Padding: On" else "Padding: Off"
            this.labelPadding.text = text
        }
        
        this.buttonOnOff.setOnClickListener {
            this.mySwitchTest.isOn = ! this.mySwitchTest.isOn
        }
        this.buttonEnableDisable.setOnClickListener {
            this.mySwitchTest.isEnabled = ! this.mySwitchTest.isEnabled
        }
    }
}
