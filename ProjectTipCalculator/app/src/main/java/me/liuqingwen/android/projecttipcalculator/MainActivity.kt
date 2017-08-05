package me.liuqingwen.android.projecttipcalculator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SeekBar
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainActivity : AppCompatActivity(), AnkoLogger
{
    
    private var value = 0f
    private var valueString = ""
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.progressTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean)
            {
                if(this@MainActivity.textValue.text.toString() != this@MainActivity.valueString)
                {
                    this@MainActivity.value = this@MainActivity.textValue.text.toString().removePrefix("$").toFloatOrNull() ?: 0f
                }
                this@MainActivity.labelTip.text = "Tip ($p1%)"
                this@MainActivity.calculateTip()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        
        this.textValue.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
            {
                this.value = this.textValue.text.toString().removePrefix("$").toFloatOrNull() ?: 0f
                this.valueString = "$${this.value}"
                this.textValue.setText(this.valueString)
                this.calculateTip()
            }
            false
        }
    }
    
    private fun calculateTip()
    {
        val tip = this.progressTip.progress * this.value / 100
        val total = this.value + tip
        this.textTip.text = String.format("%.2f", tip)
        this.textTotal.text = String.format("%.2f", total)
    }
}
