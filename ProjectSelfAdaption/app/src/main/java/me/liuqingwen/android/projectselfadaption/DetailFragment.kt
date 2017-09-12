package me.liuqingwen.android.projectselfadaption

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_detail_fragment.*
import org.jetbrains.anko.AnkoLogger
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment:Fragment(), AnkoLogger
{
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater!!.inflate(R.layout.layout_detail_fragment, container, false)!!
    }
    
    fun showPostDetail(post:Post)
    {
        this.labelTitle.text = post.title
        this.textContent.text = post.content
        this.labelAuthor.text = post.author
        this.labelDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(post.date)
        this.ratingBlogBar.rating = post.rating
    }
}