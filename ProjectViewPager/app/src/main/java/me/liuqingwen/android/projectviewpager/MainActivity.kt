package me.liuqingwen.android.projectviewpager

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), AnkoLogger
{
    companion object
    {
        const val PREFERENCE_NAME = "project_view_pager"
        private const val NO_PAGES_NEXT_TIME = "no_pages_next_time"
    }
    
    private val pages by lazy(LazyThreadSafetyMode.NONE) { arrayListOf(Page("4.1 Jelly Bean", this.getString(R.string.android_4), R.drawable.android4),
                                                                       Page("5.0 Lollipop", this.getString(R.string.android_5), R.drawable.android5),
                                                                       Page("6.0 Marshmallow", this.getString(R.string.android_6), R.drawable.android6),
                                                                       Page("7.0 Nougat", this.getString(R.string.android_7), R.drawable.android7),
                                                                       Page("8.0 Oreo", this.getString(R.string.android_8), R.drawable.android8)) }
    private val pagerAdapter by lazy(LazyThreadSafetyMode.NONE) { ViewPagerAdapter(this.supportFragmentManager, this.pages) }
    private val preference by lazy(LazyThreadSafetyMode.NONE) { this.getSharedPreferences(MainActivity.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.initUI()
        this.init()
    }
    
    private fun initUI()
    {
        this.checkBox.visibility = View.INVISIBLE
        this.imageButton.visibility = View.INVISIBLE
    }
    
    private fun init()
    {
        if (this.preference.getBoolean(MainActivity.NO_PAGES_NEXT_TIME, false))
        {
            this.startActivity<AppActivity>()
            this.finish()
            return
        }
        
        this.viewPager.adapter = this.pagerAdapter
        this.layoutTabs.setupWithViewPager(this.viewPager)
        
        this.viewPager.addOnPageChangeListener(object:ViewPager.SimpleOnPageChangeListener(){
            override fun onPageSelected(position: Int)
            {
                super.onPageSelected(position)
                if (position >= this@MainActivity.pages.size - 1 && this@MainActivity.imageButton.visibility != View.VISIBLE)
                {
                    this@MainActivity.imageButton.visibility = View.VISIBLE
                    this@MainActivity.checkBox.visibility = View.VISIBLE
                    this@MainActivity.imageButton.setOnClickListener {
                        val noViewPager = this@MainActivity.checkBox.isChecked
                        val editor = this@MainActivity.preference.edit()
                        editor.putBoolean(MainActivity.NO_PAGES_NEXT_TIME, noViewPager)
                        editor.apply()
                        
                        this@MainActivity.startActivity<AppActivity>()
                        this@MainActivity.finish()
                    }
                }
            }
        })
    }
}
