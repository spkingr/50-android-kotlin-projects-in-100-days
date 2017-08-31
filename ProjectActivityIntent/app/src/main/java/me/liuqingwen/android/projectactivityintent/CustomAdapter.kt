package me.liuqingwen.android.projectactivityintent

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_list_item.view.*

data class Post(val title:String, val url:String)

class CustomViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
{
    var imageTitle:ImageView = itemView.imageTitle
    var labelTitle:TextView = itemView.labelTitle
}

class CustomAdapter(private val context: Context, private val dataList:List<Post>, var itemClick:((View)->Unit)? = null):RecyclerView.Adapter<CustomViewHolder>()
{
    private val layoutInflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(this.context) }
    
    override fun onBindViewHolder(holder: CustomViewHolder?, position: Int)
    {
        holder?.let {
            val post = this.dataList[position]
            it.labelTitle.text = post.title
            Glide.with(this.context).load(post.url).into(it.imageTitle)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder
    {
        val view = this.layoutInflater.inflate(R.layout.layout_list_item, parent, false)
        this.itemClick?.let { view.setOnClickListener(it) }
        return CustomViewHolder(view)
    }
    override fun getItemCount(): Int
    {
        return this.dataList.size
    }
}