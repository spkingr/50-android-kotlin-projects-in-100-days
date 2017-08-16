package me.liuqingwen.android.projectrefreshrecyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_list_footer.view.*
import kotlinx.android.synthetic.main.layout_list_header.view.*
import kotlinx.android.synthetic.main.layout_list_item.view.*

data class Post(val title:String, val url:String)

class HeaderViewHolder(itemView: View?):RecyclerView.ViewHolder(itemView)
{
    val labelHeader:TextView? = itemView?.labelHeader
}

class FooterViewHolder(itemView: View?):RecyclerView.ViewHolder(itemView)
{
    val labelFooter:TextView? = itemView?.labelFooter
}

class CustomViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
{
    var imageTitle:ImageView = itemView.imageTitle
    var labelTitle:TextView = itemView.labelTitle
}

class CustomAdapter(private val context: Context, private val dataList:List<Post>):RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    companion object
    {
        val ITEM_TYPE_HEADER = -1
        val ITEM_TYPE_ITEM = 0
        val ITEM_TYPE_FOOTER = 1
    }
    private val layoutInflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(this.context) }
    private var headerView:View? = null
    private var footerView:View? = null
    
    fun addHeadView(view:View?)
    {
        this.headerView = view
    }
    
    fun addFooterView(view:View?)
    {
        this.footerView = view
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int)
    {
        holder?.let {
            when (it)
            {
                is HeaderViewHolder -> it.labelHeader?.text = "Item Count: ${this.dataList.size}"
                is FooterViewHolder -> { it.labelFooter?.text = this.context.getString(R.string.footer_text) }
                is CustomViewHolder ->
                {
                    val index = position - (if (this.headerView != null) 1 else 0)
                    val post = this.dataList[index]
                    it.labelTitle.text = post.title
                    Glide.with(this.context).load(post.url).into(it.imageTitle)
                }
                else -> {}
            }
        }
    }
    override fun getItemCount(): Int
    {
        val footOrHeadCount = 0 + (if(this.headerView == null) 0 else 1) + (if(this.footerView == null) 0 else 1)
        return this.dataList.size + footOrHeadCount
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder
    {
        return when(viewType)
        {
            CustomAdapter.ITEM_TYPE_HEADER -> HeaderViewHolder(this.headerView)
            CustomAdapter.ITEM_TYPE_FOOTER -> FooterViewHolder(this.footerView)
            else -> {
                val view = this.layoutInflater.inflate(R.layout.layout_list_item, parent, false)
                return CustomViewHolder(view)
            }
        }
    }
    
    override fun getItemViewType(position: Int): Int
    {
        if(this.headerView != null && position == 0)
        {
            return CustomAdapter.ITEM_TYPE_HEADER
        }
        if (this.footerView != null && position == this.itemCount - 1)
        {
            return CustomAdapter.ITEM_TYPE_FOOTER
        }
        
        return CustomAdapter.ITEM_TYPE_ITEM
    }
}