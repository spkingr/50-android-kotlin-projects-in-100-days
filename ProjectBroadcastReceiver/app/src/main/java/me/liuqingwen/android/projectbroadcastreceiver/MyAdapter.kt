package me.liuqingwen.android.projectbroadcastreceiver

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_item_list.view.*

/**
 * Created by Qingwen on 2017-2017-9-27, project: ProjectBroadcastReceiver.
 *
 * @Author: Qingwen
 * @DateTime: 2017-9-27
 * @Package: me.liuqingwen.android.projectbroadcastreceiver in project: ProjectBroadcastReceiver
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

data class MyContact(var name:String, var imageRes:Int, var introduction:String)

class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
{
    val labelName:TextView = this.itemView.labelName
    val imageProfile:CircleImageView = this.itemView.imageProfile
}

class MyAdapter(private val context: Context, private val dataList:ArrayList<MyContact>, var itemClick:((view:View)->Unit)? = null): RecyclerView.Adapter<MyViewHolder>()
{
    private val inflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(this.context) }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder
    {
        val view = this.inflater.inflate(R.layout.layout_item_list, parent, false)
        this.itemClick?.let { view.setOnClickListener(it) }
        return MyViewHolder(view)
    }
    
    override fun getItemCount() = this.dataList.size
    
    override fun onBindViewHolder(holder: MyViewHolder?, position: Int)
    {
        holder?.let {
            val contact = this.dataList[position]
            it.labelName.text = contact.name
            it.imageProfile.setImageResource(contact.imageRes)
        }
    }
    
}