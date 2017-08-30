package me.liuqingwen.android.projectsimpleanimation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.DecelerateInterpolator
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.displayMetrics

class MainActivity : AppCompatActivity(), AnkoLogger
{
    private var sunYStart = 0f
    private var sunYEnd = 0f
    private var duration = 10000L
    private var sunSetDelay = 2000L
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        //this.skyView.translationY = - this.skyView.height.toFloat()
        
        this.sunYStart = this.sunView.top.toFloat()
        this.sunYEnd = this.displayMetrics.heightPixels.toFloat()
        
        this.skyView.alpha = 0.0f
        
        this.sunSet()
    }
    
    private fun sunSet()
    {
        val animSun = ObjectAnimator.ofFloat(this.sunView, "y", this.sunYStart, this.sunYEnd).setDuration(this.duration)
        animSun.interpolator = DecelerateInterpolator()
        
        val animSky = ObjectAnimator.ofFloat(this.skyView, "alpha", 0.0f, 1.0f).setDuration(this.duration - 2000)
        
        val animSet = AnimatorSet()
    
        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?)
            {
                this@MainActivity.sunRise()
            }
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
        })
        
        animSet.play(animSun).with(animSky)
        animSet.startDelay = this.sunSetDelay
        animSet.start()
    }
    
    private fun sunRise()
    {
        val animSun = ObjectAnimator.ofFloat(this.sunView, "y", this.sunYEnd, this.sunYStart).setDuration(this.duration)
        //anim.interpolator = DecelerateInterpolator()
    
        val animSky = ObjectAnimator.ofFloat(this.skyView, "alpha", 1.0f, 0.0f).setDuration(this.duration - 2000)
    
        val animSet = AnimatorSet()
    
        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?)
            {
                this@MainActivity.sunSet()
            }
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
        })
    
        animSet.play(animSun).with(animSky)
        animSet.start()
    }
}
