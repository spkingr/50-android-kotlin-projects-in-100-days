package me.liuqingwen.android.projectbroadcastreceiver

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity()
{
    companion object
    {
        fun getIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
    
    private val connectivityManager by lazy(LazyThreadSafetyMode.NONE) { this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { ArrayList<MyContact>().apply {
        this.add(MyContact("Adeline", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Adeline)))
        this.add(MyContact("Alexdander", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Alexdander)))
        this.add(MyContact("Benjamin", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Benjamin)))
        this.add(MyContact("Charlotte", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Charlotte)))
        this.add(MyContact("Donovan", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Donovan)))
        this.add(MyContact("Mackenzie", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Mackenzie)))
        this.add(MyContact("Natasha", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Natasha)))
        this.add(MyContact("Michelle", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Michelle)))
        this.add(MyContact("Orlando", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Orlando)))
        this.add(MyContact("Rosemary", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Rosemary)))
        this.add(MyContact("Valentina", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Valentina)))
        this.add(MyContact("Whitney", R.mipmap.ic_launcher_round, this@MainActivity.resources.getString(R.string.Whitney)))
    } }
    private val myAdapter by lazy(LazyThreadSafetyMode.NONE) { MyAdapter(this, this.dataList) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    @Subscribe
    fun onNetworkStatusEvent(event:NetworkStatusEvent)
    {
        val netInfo = this.connectivityManager.activeNetworkInfo
        if (netInfo == null || ! netInfo.isAvailable)
        {
            IS_LOG_IN = false
            this.makeSureLogin()
        }
    }
    
    @Subscribe
    fun onLogoutEvent(event:LogoutEvent)
    {
        this.logout()
    }
    
    private fun makeSureLogin()
    {
        if (! IS_LOG_IN)
        {
            val intent = LoginActivity.getIntent(this)
            this.startActivity(intent)
            
            this.finish()
        }
    }
    
    private fun init()
    {
        this.setupLogoutButton()
        this.makeSureLogin()
        this.setupDataList()
    }
    
    private fun setupDataList()
    {
        this.myAdapter.itemClick = {
            val index = this.recyclerView.getChildAdapterPosition(it)
            val (name, imageRes, intro) = this.dataList[index]
            val intent = DetailActivity.getIntent(this, name, imageRes, intro)
            this.startActivity(intent)
        }
        this.recyclerView.adapter = this.myAdapter
        this.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        this.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
    
    override fun onResume()
    {
        super.onResume()
    }
    
    override fun onPause()
    {
        super.onPause()
    }
    
    override fun onStart()
    {
        super.onStart()
        EventBus.getDefault().register(this)
    }
    
    override fun onStop()
    {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}
