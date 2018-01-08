package me.liuqingwen.android.projectbetterpracticefragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_activity_detail.*
import org.jetbrains.anko.toast

class DetailActivity : AppCompatActivity()
{
    companion object
    {
        private const val DATA_LIST_NAME = "contact_data_list"
        private const val DATA_LIST_INDEX = "contact_data_index"
        fun getDetailIntent(context: Context, contacts:ArrayList<Contact>, index:Int) = Intent(context, DetailActivity::class.java).apply {
            this.putParcelableArrayListExtra(DetailActivity.DATA_LIST_NAME, contacts)
            this.putExtra(DetailActivity.DATA_LIST_INDEX, index)
        }
    }
    private lateinit var dataList:ArrayList<Contact>
    private lateinit var selectedContact:Contact
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_detail)
        
        this.init()
    }
    
    private fun init()
    {
        this.dataList = this.intent.getParcelableArrayListExtra(DetailActivity.DATA_LIST_NAME)
        val index = this.intent.getIntExtra(DetailActivity.DATA_LIST_INDEX, -1)
        this.selectedContact = this.dataList[index] //error while index = -1
        
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.title = this.selectedContact.name
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    
        this.viewPagerDetail.adapter = object : FragmentStatePagerAdapter(this.supportFragmentManager){
            override fun getItem(position: Int) = DetailFragment.newInstance(this@DetailActivity.dataList[position])
            override fun getCount() = this@DetailActivity.dataList.size
        }
        
        this.viewPagerDetail.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int)
            {
                super.onPageSelected(position)
                val contact = this@DetailActivity.dataList[position]
                this@DetailActivity.supportActionBar?.title = contact.name
            }
        })
        
        this.viewPagerDetail.currentItem = index
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            android.R.id.home -> {
                this.onBackPressed()
            }
            else -> {
                toast("Not implemented yet!")
            }
        }
        return super.onOptionsItemSelected(item)
    }
}