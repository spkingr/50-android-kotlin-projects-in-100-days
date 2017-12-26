package me.liuqingwen.android.projectphotowall

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.layout_activity_main.*
import kotlinx.android.synthetic.main.layout_empty_content_holder.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity()
{
    companion object
    {
        private const val LOADING_TIME_OUT = 5000
        const val VIEW_IMAGE_REQUEST_CODE = 1001
        const val ADD_IMAGE_REQUEST_CODE = 1002
    }
    
    private val viewLoader by lazy(LazyThreadSafetyMode.NONE) { this.viewStubHolder.inflate() }
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { MyAdapter(this, this.dataList){
        val index = this.recyclerView.getChildAdapterPosition(it)
        val photo = this.dataList[index]
        val intent = DetailActivity.getIntent(this, true, photo)
        this.startActivityForResult(intent, MainActivity.VIEW_IMAGE_REQUEST_CODE)
    } }
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { arrayListOf<Photo>() }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        setSupportActionBar(this.toolbar)
        this.supportActionBar?.title = "Photo Wall"
        
        this.layoutSwipeRefresh.setOnRefreshListener {
            if (this.viewLoader.labelInfo.visibility == View.VISIBLE)
            {
                this.viewLoader.labelInfo.text = this.getString(R.string.load_data_info)
            }
            this.loadData()
        }
        
        this.floatingActionButton.setOnClickListener {
            val intent = DetailActivity.getIntent(this, isView = false)
            this.startActivityForResult(intent, MainActivity.ADD_IMAGE_REQUEST_CODE)
        }
        
        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        
        this.layoutSwipeRefresh.isRefreshing = true
        this.viewLoader.labelInfo.text = this.getString(R.string.load_data_info)
        this.loadData()
    }
    
    private fun loadData()
    {
        doAsync {
            val photos = AppDatabaseHelper.getInstance(this@MainActivity).getAllPhotos()
            uiThread {
                this@MainActivity.layoutSwipeRefresh.isRefreshing = false
                if (photos.isEmpty())
                {
                    this@MainActivity.viewLoader.labelInfo.text = this@MainActivity.getString(R.string.no_data_info)
                    this@MainActivity.viewLoader.labelInfo.visibility = View.VISIBLE
                }
                else if (this@MainActivity.viewLoader.labelInfo.visibility == View.VISIBLE)
                {
                    this@MainActivity.viewLoader.labelInfo.visibility = View.INVISIBLE
                }
                this@MainActivity.dataList.clear()
                this@MainActivity.dataList.addAll(photos)
                this@MainActivity.adapter.notifyDataSetChanged()
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK)
        {
            when(requestCode)
            {
                MainActivity.VIEW_IMAGE_REQUEST_CODE, MainActivity.ADD_IMAGE_REQUEST_CODE -> {
                    this.loadData()
                    this.toast("Photos updated!")
                }
                else -> {this.toast("Not handled result!")}
            }
        }
        else if (requestCode == MainActivity.ADD_IMAGE_REQUEST_CODE)
        {
            this.toast("Action canceled.")
        }
    }
    
}
