package me.liuqingwen.android.projectsimplewebview

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.webkit.*
import android.widget.SearchView
import kotlinx.android.synthetic.main.layout_activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(),AnkoLogger
{
    companion object
    {
        const val DATA_SEARCH_ENGINE = "data_search_engine"
        private val REQUEST_CODE = 1001
    }
    
    private lateinit var searchEngineBase:String
    private lateinit var searchReplacement:String
    private var searchString:String? = null
    private var isLoading = false
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.initStrings()
        this.initToolbar()
        this.initWebview()
    }
    
    private fun initStrings()
    {
        this.searchEngineBase = this.getString(R.string.search_engine)
        this.searchReplacement = this.getString(R.string.search_replacement)
    }
    
    private fun initToolbar()
    {
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.setLogo(R.drawable.globe)
    
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            this.webView.isNestedScrollingEnabled = true
        }
    }
    
    private fun doSearch()
    {
        if (this.isLoading)
        {
            return
        }
        
        val s = this.searchString
        if (s != null && s.isNotBlank())
        {
            this.toolbar.title = s
            
            val url = this.searchEngineBase.replace(this.searchReplacement, s)
            this.webView.loadUrl(url)
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        this.menuInflater.inflate(R.menu.menu_tool_bar, menu)
        val menuSearch = menu?.findItem(R.id.menuSearch)
        val searchView = menuSearch?.actionView as? SearchView
        searchView?.setIconifiedByDefault(true) //???
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean
            {
                if (query == null || query.isBlank())
                {
                    this@MainActivity.toast("Nothing to search!")
                    return true
                }
                this@MainActivity.searchString = query
                this@MainActivity.doSearch()
                
                //position important!
                searchView.onActionViewCollapsed()
                
                return false
            }
            override fun onQueryTextChange(s: String?): Boolean
            {
                this@MainActivity.searchString = s
                return false
            }
        })
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            R.id.menuRefresh -> {
                if (this.isLoading)
                {
                    this.webView.stopLoading()
                    this.isLoading = false
                }
                this.doSearch()
            }
            R.id.menuDefaultEngine -> {
                if (this.isLoading)
                {
                    this.webView.stopLoading()
                    this.isLoading = false
                }
                
                val intent = SearchEngineActivity.getIntent(this, this.searchEngineBase)
                this.startActivityForResult(intent, MainActivity.REQUEST_CODE)
            }
            R.id.menuStop    -> {
                if (this.isLoading)
                {
                    this.webView.stopLoading()
                }
            }
            else             -> { toast("Not implemented yet!") }
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == MainActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            data?.getStringExtra(MainActivity.DATA_SEARCH_ENGINE)?.let { this.searchEngineBase = if (it.isBlank()) this.searchEngineBase else it }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.webView.canGoBack())
        {
            this.webView.goBack()
            return true
        }
        
        return super.onKeyDown(keyCode, event)
    }
    
    override fun onDestroy()
    {
        super.onDestroy()
        
        this.webView.destroy()
    }
    
    private fun initWebview()
    {
        val settings = this.webView.settings
        settings.javaScriptEnabled = true
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
    
        this.webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?)
            {
                this@MainActivity.isLoading = false
                super.onPageFinished(view, url)
            }
        
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?)
            {
                this@MainActivity.isLoading = true
                super.onPageStarted(view, url, favicon)
            }
        
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?)
            {
                this@MainActivity.isLoading = false
                super.onReceivedError(view, request, error)
            }
        
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean
            {
                this@MainActivity.isLoading = false
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        
        this.webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean
            {
                return super.onJsAlert(view, url, message, result)
            }
    
            override fun onProgressChanged(view: WebView?, newProgress: Int)
            {
                info("Progress: $newProgress%")
                super.onProgressChanged(view, newProgress)
            }
    
            override fun onReceivedTitle(view: WebView?, title: String?)
            {
                info("Title: $title")
                super.onReceivedTitle(view, title)
            }
    
            override fun onReceivedIcon(view: WebView?, icon: Bitmap?)
            {
                info("icon received")
                super.onReceivedIcon(view, icon)
            }
        }
    }
    
}
