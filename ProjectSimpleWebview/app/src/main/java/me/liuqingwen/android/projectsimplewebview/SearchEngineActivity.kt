package me.liuqingwen.android.projectsimplewebview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_activity_search_engine.*
import org.jetbrains.anko.toast

class SearchEngineActivity : AppCompatActivity()
{
    companion object
    {
        val SEARCH_ENGINE_EXTRA = "search_engine_data"
        val PREFERENCES_NAME = "search_engine"
        val PREFERENCES_STRING_SET_NAME = "search_engine_list"
        
        fun getIntent(context: Context, engine:String) = Intent(context, SearchEngineActivity::class.java).apply { this.putExtra(SearchEngineActivity.SEARCH_ENGINE_EXTRA, engine) }
    }
    
    private val preferences by lazy { this.getSharedPreferences(SearchEngineActivity.PREFERENCES_NAME, Context.MODE_PRIVATE) }
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { ArrayList<SearchEngine>() }
    private val myAdapter by lazy(LazyThreadSafetyMode.NONE) { MyAdapter(this, this.dataList) }
    private var isSelectionChanged = false
    private lateinit var selectedEngine:String
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_search_engine)
        
        this.initGUI()
        this.init()
    }
    
    private fun initGUI()
    {
        this.buttonAdd.setOnClickListener {
            val url = this.textSearchEngine.text.toString().trim()
            if (url.isBlank())
            {
                this.textSearchEngine.text.clear()
                this.toast("No search engine added.")
            }
            else
            {
                val added = this.dataList.asSequence().firstOrNull{ it.url == url } != null
                if (added)
                {
                    this.toast("Already added!")
                }
                else
                {
                    val name = "Default Name" //should use regExp here...
                    this.dataList.add(SearchEngine(name, url, false))
                    this.myAdapter.notifyDataSetChanged()
                
                    this.saveData()
                }
            }
        }
    
        this.buttonFloatingBack.setOnClickListener {
            if (this.isSelectionChanged)
            {
                val data = Intent()
                data.putExtra(MainActivity.DATA_SEARCH_ENGINE, this.selectedEngine)
                this.setResult(Activity.RESULT_OK, data)
            }
            this.finish()
        }
    }
    
    private fun init()
    {
        this.myAdapter.itemClick = {
            val position = this.listUrls.getChildAdapterPosition(it)
            val engine = this.dataList[position]
            if (! engine.selected)
            {
                this.isSelectionChanged = true
                this.selectedEngine = engine.url
                
                this.dataList.forEach { it.selected = false }
                engine.selected = true
                this.myAdapter.notifyDataSetChanged()
            }
        }
        
        this.myAdapter.longClick = {
            val position = this.listUrls.getChildAdapterPosition(it)
            val engine = this.dataList[position]
            AlertDialog.Builder(this).setTitle("Warning").setMessage("Do you want to delete this item?")
                    .setCancelable(true)
                    .setNegativeButton("No, Keep"){_,_->}
                    .setPositiveButton("Yes, Delete"){_, _->
                        this.dataList.removeAt(position)
                        if(engine.selected)
                        {
                            if (this.dataList.size >= 1)
                            {
                                this.dataList.first().selected = true
                                this.selectedEngine = this.dataList.first().url
                            }
                            else
                            {
                                this.selectedEngine = ""
                            }
                            this.isSelectionChanged = true
                        }
                        this.myAdapter.notifyDataSetChanged()
                        
                        this.saveData()
                    }
                    .show()
            true
        }
        
        val selectedEngine = this.intent?.getStringExtra(SearchEngineActivity.SEARCH_ENGINE_EXTRA) ?: ""
        
        val list = this.preferences.getStringSet(SearchEngineActivity.PREFERENCES_STRING_SET_NAME, setOf(this.getString(R.string.search_engine)))
        list.forEach {
            val name = "Default Name" //should use regExp here...
            this.dataList.add(SearchEngine(name, it, selectedEngine == it))
        }
        
        this.listUrls.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        this.listUrls.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        this.listUrls.adapter = this.myAdapter
    }
    
    private fun saveData()
    {
        val data = arrayListOf<String>()
        this.dataList.forEach { data.add(it.url) }
        val edit = this.preferences.edit()
        edit.putStringSet(SearchEngineActivity.PREFERENCES_STRING_SET_NAME, data.toSet())
        edit.apply()
    }
    
    override fun onBackPressed()
    {
        if (this.isSelectionChanged)
        {
            AlertDialog.Builder(this).setTitle("Are you sure?").setMessage("Configuration has changed, are you sure to discard it?")
                    .setCancelable(true)
                    .setNegativeButton("Stay"){_,_->}
                    .setPositiveButton("Discard"){_, _->
                        super.onBackPressed()
                    }
                    .show()
        }
        else
        {
            super.onBackPressed()
        }
    }
}
