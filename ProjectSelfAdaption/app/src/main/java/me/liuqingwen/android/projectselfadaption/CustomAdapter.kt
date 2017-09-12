package me.liuqingwen.android.projectselfadaption

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Qingwen on 2017-2017-9-10, project: ProjectSelfAdaption.
 *
 * @Author: Qingwen
 * @DateTime: 2017-9-10
 * @Package: me.liuqingwen.android.projectselfadaption in project: ProjectSelfAdaption
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

data class Post(var title:String, var content:String, var author:String, var date:Date, var rating:Float = 5.0f)

class CustomViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
{
    val labelTitle:TextView = itemView.labelTitle
    val labelDate:TextView = itemView.labelDate
}

class CustomAdapter(context:Context, private val dataList:List<Post>, var itemClick:((view:View)->Unit)? = null):RecyclerView.Adapter<CustomViewHolder>()
{
    private val layoutInflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(context) }
    var dateLabelVisible:Boolean = true
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder
    {
        val view = this.layoutInflater.inflate(R.layout.layout_list_item, parent, false)
        this.itemClick?.let { view.setOnClickListener(it) }
        return CustomViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: CustomViewHolder?, position: Int)
    {
        val post = this.dataList[position]
        holder?.let {
            it.labelTitle.text = post.title
            it.labelDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(post.date)
            
            it.labelDate.visibility = if (this.dateLabelVisible) View.VISIBLE else View.GONE
        }
    }
    
    override fun getItemCount() = this.dataList.size
    
}