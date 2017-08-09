package me.liuqingwen.android.projectrecycleview

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_activity_main.*
import kotlinx.android.synthetic.main.layout_list_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity()
{
 
    private val words by lazy { arrayOf("Adapter: A subclass of RecyclerView.Adapter responsible for providing views that represent items in a dataList set.",
                                        "Position: The position of a dataList item within an Adapter.",
                                        "Index: The index of an attached child view as used in a call to getChildAt(int). Contrast with Position.",
                                        "Binding: The process of preparing a child view to display dataList corresponding to a position within the adapter.",
                                        "Recycle (view): A view previously used to display dataList for a specific adapter position may be placed in a cache for later reuse to display the same type of dataList again later. This can drastically improve performance by skipping initial layout inflation or construction.",
                                        "Scrap (view): A child view that has entered into a temporarily detached state during layout. Scrap views may be reused without becoming fully detached from the parent RecyclerView, either unmodified if no rebinding is required or modified by the adapter if the view was considered dirty.",
                                        "Dirty (view): A child view that must be rebound by the adapter before being displayed.") }
    private val images by lazy { arrayOf(R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6
                                         , R.drawable.image7, R.drawable.image8, R.drawable.image9, R.drawable.image11, R.drawable.image12
                                         , R.drawable.image13, R.drawable.image14, R.drawable.image15, R.drawable.image16) }
    private val random by lazy { Random() }
    private val dataList by lazy { this.produceData(16) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)
        
        this.init()
    }
    
    private fun init()
    {
        val recycleView = this.recycleView
        recycleView.adapter = MyAdapter(this, this.dataList)
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recycleView.layoutManager = layoutManager
    }
    
    private fun produceData(size:Int):ArrayList<Pair<String, Int>>
    {
        val data = ArrayList<Pair<String, Int>>(size)
        for (i in 0..size)
        {
            val index1 = this.random.nextInt(this.words.size)
            val index2 = this.random.nextInt(this.images.size)
            data.add((this.words[index1] to this.images[index2]))
        }
        return data
    }
}

class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
{
    var imageView:ImageView = itemView.imageView
    var labelTitle:TextView = itemView.labelTitle
}

class MyAdapter(val context:Context, val dataList:ArrayList<Pair<String, Int>>): RecyclerView.Adapter<MyViewHolder>()
{
    private val inflater by lazy { LayoutInflater.from(this.context) }
    
    override fun onBindViewHolder(holder: MyViewHolder?, position: Int)
    {
        val (title, resourceId) = this.dataList[position]
        holder?.imageView?.setImageResource(resourceId)
        holder?.labelTitle?.text = title
    }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder
    {
        val view = this.inflater.inflate(R.layout.layout_list_item, parent, false)
        val holder = MyViewHolder(view)
        return holder
    }
    
    override fun getItemCount(): Int
    {
        return this.dataList.size
    }
    
}
