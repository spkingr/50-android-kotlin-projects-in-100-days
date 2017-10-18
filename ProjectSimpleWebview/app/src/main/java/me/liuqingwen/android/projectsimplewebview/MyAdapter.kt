package me.liuqingwen.android.projectsimplewebview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_item_search_list.view.*

/**
 * Created by Qingwen on 2017-2017-10-18, project: ProjectSimpleWebview.
 *
 * @Author: Qingwen
 * @DateTime: 2017-10-18
 * @Package: me.liuqingwen.android.projectsimplewebview in project: ProjectSimpleWebview
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

data class SearchEngine(var name:String, var url:String, var selected:Boolean = false)

class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
{
    val imageSearchEngine:ImageView = this.itemView.imageSearchIcon
    val labelSearchEngine:TextView = this.itemView.textSearchEngine
    val imageSelected:ImageView = this.itemView.imageSelected
}

class MyAdapter(private val context: Context, private val dataList:ArrayList<SearchEngine>, var itemClick:((view:View)->Unit)? = null, var longClick:((view:View)->Boolean)? = null): RecyclerView.Adapter<MyViewHolder>()
{
    private val inflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(this.context) }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder
    {
        val view = this.inflater.inflate(R.layout.layout_item_search_list, parent, false)
        this.itemClick?.let { view.setOnClickListener(it) }
        this.longClick?.let { view.setOnLongClickListener(it) }
        return MyViewHolder(view)
    }
    
    override fun getItemCount() = this.dataList.size
    
    override fun onBindViewHolder(holder: MyViewHolder?, position: Int)
    {
        holder?.let {
            val engine = this.dataList[position]
            it.labelSearchEngine.text = engine.url
            it.imageSelected.visibility = if (engine.selected) View.VISIBLE else View.INVISIBLE
        }
    }
    
}