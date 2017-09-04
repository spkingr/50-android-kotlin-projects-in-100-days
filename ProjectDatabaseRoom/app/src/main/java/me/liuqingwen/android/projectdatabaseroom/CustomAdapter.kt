package me.liuqingwen.android.projectdatabaseroom

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_list_item.view.*

/**
 * Created by Qingwen on 2017-9-4.
 */

class CustomViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
{
    val labelId = itemView.labelId!!
    val labelTitle = itemView.labelTitle!!
}

class CustomAdapter(private val context:Context, private val dataList:List<Post>, var itemClick:((view:View)->Unit)? = null):RecyclerView.Adapter<CustomViewHolder>()
{
    private val layoutInflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(this.context) }
    
    override fun onBindViewHolder(holder: CustomViewHolder?, position: Int)
    {
        holder?.let {
            val post = this.dataList[position]
            it.labelId.text = "${post.id}:"
            it.labelTitle.text = post.title
        }
    }
    
    override fun getItemCount() = this.dataList.size
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int):CustomViewHolder
    {
        val view = this.layoutInflater.inflate(R.layout.layout_list_item, parent, false)
        this.itemClick?.let { view.setOnClickListener(it) }
        return CustomViewHolder(view)
    }
}