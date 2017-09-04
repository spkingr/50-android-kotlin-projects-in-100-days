package me.liuqingwen.android.projectdatabaseroom

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainActivity : AppCompatActivity(), AnkoLogger
{
    private val db by lazy { AppDatabaseHelper.getInstance(this.applicationContext) }
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { ArrayList<Post>() }
    private val adapter by lazy { CustomAdapter(this, this.dataList) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.adapter.itemClick = {
            val index = this.recyclerView.getChildAdapterPosition(it)
            info("debug.........................................................  $index")
        }
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val dividerDecoration = DividerItemDecoration(this, layoutManager.orientation)
        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.addItemDecoration(dividerDecoration)
        this.recyclerView.adapter = this.adapter
        
        this.loadData()
    }
    
    private fun loadData()
    {
        this.dataList.addAll(this.db.getAll())
        this.adapter.notifyDataSetChanged()
    }
}
