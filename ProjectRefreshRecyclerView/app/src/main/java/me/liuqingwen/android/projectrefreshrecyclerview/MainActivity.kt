package me.liuqingwen.android.projectrefreshrecyclerview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.doAsync
import java.util.*

class MainActivity : AppCompatActivity()
{
    companion object
    {
        private val POST_URLS = arrayOf("http://liuqingwen.me/blog/2017/08/10/talk-about-the-importance-of-regexp-in-programming/cover.jpg",
                                          "http://liuqingwen.me/blog/2017/08/03/learning-notes-of-tanks-tutorial-in-unity3d/cover.jpg",
                                          "http://liuqingwen.me/blog/2017/07/31/understanding-coordinate-system-in-unity3d/cover.jpg",
                                          "http://liuqingwen.me/blog/2017/07/04/solve-several-problems-of-kotlin-plugin-in-intellij-idea/cover.jpg",
                                          "http://liuqingwen.me/blog/2017/06/30/translation-of-clean-code-with-kotlin/cover.jpg",
                                          "http://liuqingwen.me/blog/2017/06/25/leanrning-notes-of-survival-shooter-tutorial-in-unity3d/cover.jpg",
                                          "http://liuqingwen.me/blog/2017/06/20/object-vs-companion-object-in-kotlin/cover.jpg",
                                          "http://liuqingwen.me/blog/2017/06/14/talking-about-Java-concurrency-with-lock-or-unlock/cover.jpg",
                                          "http://liuqingwen.me/blog/2017/06/11/solve-several-problems-of-gradle-in-android-studio-3-0-on-mac/cover.jpg",
                                          "http://liuqingwen.me/blog/2017/06/03/what-do-17-GDEs-for-Android-think-about-Kotlin/cover.jpg")
        private val POST_TITLES = arrayOf("Talking about the importance of regexp in programming",
                                        "Learning notes of Tanks tutorial in Unity3D official course",
                                        "Understanding the 4 coordinate systems in Unity3D",
                                        "Solve several problems while using Kotlin plugin in IntelliJ IDEA",
                                        "Clean Code With Kotlin",
                                        "Learning notes of Survival Shooter tutorial in Unity3D official course",
                                        "Talking about the differences of object and companion object in Kotlin",
                                        "Talking about Java concurrency with lock or unlock",
                                        "Solve several problems occured with Gradle in Android Studio 3.0 Canary 3 on Mac",
                                        "What do 17 Google Developers Experts for Android think about Kotlin")
    }
    
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { ArrayList<Post>() }
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { CustomAdapter(this, this.dataList) }
    private var isRefreshing = false
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
        this.loadData()
    }
    
    private fun loadData()
    {
        if (this.isRefreshing)
        {
            return
        }
        
        this.isRefreshing = true
        doAsync {
            val random = Random()
            val count = random.nextInt(3)
            for (i in 0..count)
            {
                val index = random.nextInt(MainActivity.POST_TITLES.size)
                this@MainActivity.dataList.add(Post(MainActivity.POST_TITLES[index], MainActivity.POST_URLS[index]))
            }
            Thread.sleep(1000)
            runOnUiThread {
                this@MainActivity.isRefreshing = false
                this@MainActivity.layoutSwipeRefresh.isRefreshing = false
                if (count == 0)
                {
                    Snackbar.make(this@MainActivity.buttonFloatingAction, "No data updated.", Snackbar.LENGTH_LONG).setAction("Done") {  }.show()
                }
                else
                {
                    this@MainActivity.adapter.notifyDataSetChanged()
                }
            }
        }
    }
    
    private fun init()
    {
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.left)
        
        this.layoutSwipeRefresh.setOnRefreshListener {
            this.loadData()
        }
        
        this.buttonFloatingAction.setOnClickListener {
            this.recyclerView.scrollToPosition(0)
        }
    
        this.adapter.addHeadView(LayoutInflater.from(this).inflate(R.layout.layout_list_header, null))
        this.adapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.layout_list_footer, null))
        val layoutManager = GridLayoutManager(this, 2)
        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = this.adapter
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        this.menuInflater.inflate(R.menu.menu_toobar, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            android.R.id.home -> {
                AlertDialog.Builder(this).setMessage("Are you sure to quit?").setCancelable(true)
                        .setPositiveButton("Quit") { _, _-> this.finish(); }
                        .setNegativeButton("Cancel") { _, _ -> }
                        .show()
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }
}


