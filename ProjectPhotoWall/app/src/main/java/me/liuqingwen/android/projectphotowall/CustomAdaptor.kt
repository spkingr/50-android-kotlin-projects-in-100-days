package me.liuqingwen.android.projectphotowall

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.layout_image_item.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Qingwen on 2017-2017-12-24, project: ProjectPhotoWall.
 *
 * @Author: Qingwen
 * @DateTime: 2017-12-24
 * @Package: me.liuqingwen.android.projectphotowall in project: ProjectPhotoWall
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */


class MyItemView(itemView:View):RecyclerView.ViewHolder(itemView)
{
    val imageView:ImageView = itemView.imageView
}

class MyAdapter(private val context:Context, private val dataList:List<Photo>, var listener:((View)->Unit)? = null):RecyclerView.Adapter<MyItemView>()
{
    private val inflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(this.context) }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyItemView
    {
        val view = this.inflater.inflate(R.layout.layout_image_item, parent, false)
        this.listener?.let { view.setOnClickListener(it) }
        return MyItemView(view)
    }
    
    override fun onBindViewHolder(holder: MyItemView?, position: Int)
    {
        holder?.let {
            val photo = this.dataList[position]
            displayImageFromUrl(this.context, photo.url, it.imageView)
        }
    }
    
    override fun getItemCount() = this.dataList.size
    
}

