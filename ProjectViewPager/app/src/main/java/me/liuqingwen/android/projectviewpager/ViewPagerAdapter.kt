package me.liuqingwen.android.projectviewpager

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by Qingwen on 2017-2017-12-16, project: ProjectViewPager.
 *
 * @Author: Qingwen
 * @DateTime: 2017-12-16
 * @Package: me.liuqingwen.android.projectviewpager in project: ProjectViewPager
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

data class Page(val title:String, val content:String, val image:Int)

class ViewPagerAdapter(fragmentManager: FragmentManager, private val pages:List<Page>):FragmentStatePagerAdapter(fragmentManager)
{
    override fun getItem(position: Int): Fragment
    {
        return ViewPagerFragment.newInstance(this.pages[position])
    }
    
    override fun getCount(): Int
    {
        return this.pages.size
    }
    
    override fun getPageTitle(position: Int): CharSequence
    {
        return this.pages[position].title
    }
}