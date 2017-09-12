package me.liuqingwen.android.projectselfadaption

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_activity_main.*

class MainActivity : AppCompatActivity()
{
    private var isTwoPane = false
    private var currentSelection = 0
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.isTwoPane = this.__layout_detail__ != null
        
        val listFragment = this.fragmentList as ListFragment
        listFragment.setLabelDateVisible(! this.isTwoPane)
        
        if (this.isTwoPane)
        {
            val detailFragment = this.fragmentDetail as DetailFragment
            detailFragment.showPostDetail(listFragment.getPostAt(this.currentSelection))
            listFragment.onPostClickHandler = {post, index->
                if (this.currentSelection != index)
                {
                    this.currentSelection = index
                    detailFragment.showPostDetail(post)
                }
            }
        }
        else
        {
            listFragment.onPostClickHandler = { (title, content, author, date, rating), _ ->
                val intent = DetailActivity.getIntent(this, title, content, author, date.time, rating)
                this.startActivity(intent)
            }
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?)
    {
        super.onSaveInstanceState(outState, outPersistentState)
    }
}
