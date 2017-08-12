package me.liuqingwen.android.projectbasicmaterialdesign

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_activity_main.*
import kotlinx.android.synthetic.main.recycler_list_item.view.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity()
{
    private val dataUrl = "http://liuqingwen.me/data/get-images-json.php?type=json&delay=0"
    private val httpClient by lazy { OkHttpClient.Builder().build() }
    private val dataObservable by lazy {
        Observable.create(ObservableOnSubscribe<Response> {
            val request = Request.Builder().get().url(this.dataUrl).build()
            try
            {
                val response = this.httpClient.newCall(request).execute()
                it.onNext(response)
                it.onComplete()
            }
            catch(e: Exception)
            {
                //it.onError(e)
                this.onLoadError()
            }
        })
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
        this.loadData()
    }
    
    private fun onLoadError()
    {
        Snackbar.make(this.floatingActionButton, "Error Loading.", Snackbar.LENGTH_INDEFINITE).setAction("Reload") { this.loadData() }.show()
    }
    
    private fun loadData()
    {
        this.dataObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (it.isSuccessful)
            {
                val content = it.body()?.string()
                val type = object : TypeToken<List<MyItem>>() {}.type
                try
                {
                    val items = Gson().fromJson<List<MyItem>>(content, type)
                    this.displayItems(items)
                }
                catch(e: Exception)
                {
                    this.onLoadError()
                }
            }else
            {
                this.onLoadError()
            }
        }
    }
    
    private fun displayItems(items: List<MyItem>?)
    {
        if (items == null)
        {
            this.onLoadError()
            return
        }
    
        Snackbar.make(this.floatingActionButton, "Is Done!", Snackbar.LENGTH_LONG).setAction("OK") {  }.show()
        
        val adapter = MyAdapter(this, items)
        this.recyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(this, 2)
        this.recyclerView.layoutManager = layoutManager
    }
    
    private fun init()
    {
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.menu)
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        this.menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            android.R.id.home -> { this.layoutDrawer.openDrawer(Gravity.START) }
            R.id.menuVideoCall -> {}
            R.id.menuUpload -> {}
            R.id.menuGlobe -> {}
            R.id.menuPlus -> {}
            else -> this.toast("Not implemented yet!")
        }
        return super.onOptionsItemSelected(item)
    }
}

data class MyItem(val url:String, val title:String)

class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
{
    val labelTitle:TextView = itemView.labelTitle
    val imageTitle:ImageView = itemView.imageTitle
}

class MyAdapter(val context:Context, val dataList:List<MyItem>):RecyclerView.Adapter<MyViewHolder>()
{
    private val inflater by lazy { LayoutInflater.from(this.context) }
    
    override fun onBindViewHolder(holder: MyViewHolder?, position: Int)
    {
        val item = this.dataList[position]
        holder?.labelTitle?.text = item.title
        Glide.with(this.context).load(item.url).into(holder?.imageTitle)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder
    {
        val view = this.inflater.inflate(R.layout.recycler_list_item, parent, false)
        val viewHolder = MyViewHolder(view)
        return viewHolder
    }
    
    override fun getItemCount(): Int
    {
        return this.dataList.size
    }
    
}
