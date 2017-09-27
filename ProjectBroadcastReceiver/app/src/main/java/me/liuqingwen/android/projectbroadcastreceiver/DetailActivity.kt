package me.liuqingwen.android.projectbroadcastreceiver

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_activity_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.toast

class DetailActivity : AppCompatActivity()
{
    companion object
    {
        private const val CONTACT_NAME = "CONTACT_NAME"
        private const val CONTACT_IMAGE = "CONTACT_IMAGE"
        private const val CONTACT_INTRO = "CONTACT_INTRO"
        fun getIntent(context: Context, name:String, image:Int, intro:String) = Intent(context, DetailActivity::class.java).apply {
            this.putExtra(DetailActivity.CONTACT_NAME, name)
            this.putExtra(DetailActivity.CONTACT_IMAGE, image)
            this.putExtra(DetailActivity.CONTACT_INTRO, intro)
        }
    }
    
    private val connectivityManager by lazy(LazyThreadSafetyMode.NONE) { this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_detail)
        
        this.init()
    }
    
    private fun init()
    {
        this.setupLogoutButton()
        
        this.supportActionBar?.setHomeButtonEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        val name = this.intent.getStringExtra(DetailActivity.CONTACT_NAME)
        val imageRes = this.intent.getIntExtra(DetailActivity.CONTACT_IMAGE, R.mipmap.ic_launcher_round)
        val introduction = this.intent.getStringExtra(DetailActivity.CONTACT_INTRO)
        
        this.labelNameDetail.text = name
        this.imageProfileDetail.setImageResource(imageRes)
        this.labelContentDetail.text = introduction
    }
    
    @Subscribe
    fun onNetworkStatusEvent(event:NetworkStatusEvent)
    {
        val netInfo = this.connectivityManager.activeNetworkInfo
        if (netInfo == null || ! netInfo.isAvailable)
        {
            IS_LOG_IN = false
            this.logout()
        }
    }
    
    @Subscribe
    fun onLogoutEvent(event:LogoutEvent)
    {
        this.logout()
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
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            android.R.id.home -> this.finish()
            else -> toast("Not implemented yet!")
        }
        return true
    }
}
