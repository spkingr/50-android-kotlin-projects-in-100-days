package me.liuqingwen.android.projectparcelabledata

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity()
{
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { DatabaseHelper.getInstance(this).getAllContacts() }
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { MyAdapter(this, this.dataList, {
        val index = this.recyclerView.getChildAdapterPosition(it)
        val contact = this.dataList[index]
        this.startActivity(DetailActivity.getDetailIntent(this, contact))
    }) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.title = "Contact List"
        
        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        this.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}
