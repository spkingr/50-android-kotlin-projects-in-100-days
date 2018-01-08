package me.liuqingwen.android.projectbetterpracticefragment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Qingwen on 2018-2018-1-2, project: ProjectBetterPracticeFragment.
 *
 * @Author: Qingwen
 * @DateTime: 2018-1-2
 * @Package: me.liuqingwen.android.projectbetterpracticefragment in project: ProjectBetterPracticeFragment
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class ContactViewItem(itemView:View):RecyclerView.ViewHolder(itemView)
{
    val imageHead:ImageView = itemView.imageHead
    val textName:TextView = itemView.textName
    val textBirthday:TextView = itemView.textBirthday
    val textAddress:TextView = itemView.textAddress
    val checkboxStar:CheckBox = itemView.checkboxStar
}

class MyAdapter(context:Context, private val dataList:List<Contact>, var listener:((View)->Unit)? = null):RecyclerView.Adapter<ContactViewItem>()
{
    private val inflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(context) }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContactViewItem
    {
        val view = this.inflater.inflate(R.layout.layout_list_item, parent, false)
        this.listener?.let { view.setOnClickListener(it) }
        return ContactViewItem(view)
    }
    
    override fun onBindViewHolder(holder: ContactViewItem?, position: Int)
    {
        holder?.let {
            val contact = this.dataList[position]
            it.textName.text = contact.name
            it.textBirthday.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(contact.birthday))
            it.textAddress.text = contact.address
            it.checkboxStar.isChecked = contact.isStarContact
            
            it.checkboxStar.setOnCheckedChangeListener { _, isChecked ->
                contact.isStarContact = isChecked
            }
        }
    }
    
    override fun getItemCount() = this.dataList.size
}