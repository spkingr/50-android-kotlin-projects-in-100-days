package me.liuqingwen.android.projectrecycleimageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_activity_main.*
import kotlinx.android.synthetic.main.recycler_list_item.view.*
import kotlinx.android.synthetic.main.viewstub_loader.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), AnkoLogger
{
    companion object
    {
        private const val DATA_URL_PATH = "http://liuqingwen.me/data/get-images-json.php?type=json&delay="
        private var LOAD_DELAY = 3;
    }
    
    private lateinit var viewLoader:View
    private val dataList by lazy { arrayListOf<Post>() }
    private val adapter by lazy { MyAdapter(this, this.dataList) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        this.viewLoader = this.viewstubLoader.inflate()
        this.loadData(MainActivity.DATA_URL_PATH + MainActivity.LOAD_DELAY)
    
        this.recycleImageView.adapter = this.adapter
        this.recycleImageView.layoutManager = GridLayoutManager(this, 2)
    }
    
    private fun displayData()
    {
        this.adapter.notifyDataSetChanged()
    }
    
    private fun loadData(path:String)
    {
        this.viewLoader.buttonReload.visibility = View.INVISIBLE
        this.viewLoader.progressLoader.visibility = View.VISIBLE
        
        doAsync {
            val stringBuilder = StringBuilder()
            
            try
            {
                val url = URL(path)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = MainActivity.LOAD_DELAY * 1000 + 3000
                connection.requestMethod = "GET"
                if (connection.responseCode == 200)
                {
                    connection.inputStream.bufferedReader().lineSequence().forEach {
                        stringBuilder.append(it)
                    }
                }
            }
            catch(e: Exception)
            {
                e.printStackTrace()
            }
            
            uiThread {
                if (stringBuilder.isBlank())
                {
                    it.viewLoader.buttonReload.visibility = View.VISIBLE
                    it.viewLoader.progressLoader.visibility = View.INVISIBLE
                    it.viewLoader.buttonReload.setOnClickListener {
                        this@MainActivity.loadData(MainActivity.DATA_URL_PATH + MainActivity.LOAD_DELAY)
                    }
                    return@uiThread
                }
                
                it.viewLoader.visibility = View.INVISIBLE
                it.dataList.clear()
                val content = stringBuilder.toString()
                val jsonParser = JSONTokener(content)
                val array = jsonParser.nextValue() as JSONArray
                for (i in 0..array.length() - 1)
                {
                    val item = array[i] as JSONObject
                    val urlPath = item.getString("url")
                    val title = item.getString("title")
                    it.dataList.add(Post(urlPath, title))
                }
                
                it.displayData()
            }
        }
    }
}

data class Post(val url:String, val title:String, var bitmap: Bitmap? = null)

class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
{
    val itemImage = itemView.imageTitle as ImageView
    val itemTitle = itemView.textTitle as TextView
}

class MyAdapter(context:Context, val dataList:List<Post>):RecyclerView.Adapter<MyViewHolder>()
{
    private val inflater by lazy { LayoutInflater.from(context) }
    
    override fun onBindViewHolder(holder: MyViewHolder?, position: Int)
    {
        val item = this.dataList[position]
        holder?.itemTitle?.text = item.title
        if (item.bitmap == null)
        {
            val path = item.url
            doAsync {
                val url = URL(path)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 8000;
                connection.requestMethod = "GET"
                var inputStream:InputStream? = null
                if (connection.responseCode == 200)
                {
                    inputStream = connection.inputStream.buffered()
                }
                val bytes = inputStream?.readBytes()
        
                uiThread {
                    bytes?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        item.bitmap = bitmap
                        holder?.itemImage?.setImageBitmap(bitmap)
                    }
                }
            }
        }else
        {
            holder?.itemImage?.setImageBitmap(item.bitmap)
        }
    }
    
    override fun getItemCount(): Int
    {
        return this.dataList.count()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder
    {
        val view = this.inflater.inflate(R.layout.recycler_list_item, parent, false)
        return MyViewHolder(view)
    }
}