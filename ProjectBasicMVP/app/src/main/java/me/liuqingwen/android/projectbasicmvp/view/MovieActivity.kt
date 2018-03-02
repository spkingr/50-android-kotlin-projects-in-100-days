package me.liuqingwen.android.projectbasicmvp.view

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import me.liuqingwen.android.projectbasicmvp.ui.*
import org.jetbrains.anko.find
import org.jetbrains.anko.setContentView

/**
 * Created by Qingwen on 2018-2-16, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-16
 * @Package: me.liuqingwen.android.projectbasicmvp.model in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class MovieActivity : AppCompatActivity()
{
    private val fragmentList by lazy(LazyThreadSafetyMode.NONE) { MovieListFragment.newInstance() }
    private val fragmentSearch by lazy(LazyThreadSafetyMode.NONE) { MovieSearchFragment.newInstance() }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        MovieUI().setContentView(this)
        
        val pages = arrayOf<Fragment>(this.fragmentList, this.fragmentSearch)
        
        val toolbar = this.find<Toolbar>(ID_LAYOUT_TOOLBAR)
        this.setSupportActionBar(toolbar)
        this.supportActionBar?.title = (pages[0] as BasicListFragment).title
        
        val viewPager = this.find<ViewPager>(ID_LAYOUT_VIEWPAGER)
        viewPager.adapter = object : FragmentPagerAdapter(this.supportFragmentManager) {
            override fun getItem(position: Int) = pages[position]
            override fun getCount() = pages.size
            override fun getPageTitle(position: Int) = (pages[position] as? BasicListFragment)?.title ?: "Page $position"
        }
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int)
            {
                super.onPageSelected(position)
                val fragment = pages[position] as? BasicListFragment
                fragment?.title?.let { this@MovieActivity.supportActionBar?.title = it }
            }
        })
        
        val tabLayout = this.find<TabLayout>(ID_LAYOUT_TAB)
        tabLayout.setupWithViewPager(viewPager, false)
        
        val buttonToTop = this.find<FloatingActionButton>(ID_BUTTON_UP)
        buttonToTop.setOnClickListener {
            val fragment = pages[viewPager.currentItem] as? BasicListFragment
            fragment?.scrollToTop()
        }
        
        val appBarLayout = this.find<AppBarLayout>(ID_LAYOUT_APPBAR)
        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            val fragment = pages[viewPager.currentItem] as? BasicListFragment
            fragment?.onAppbarScrollChange(appBarLayout.height, verticalOffset)
        }
    }
}