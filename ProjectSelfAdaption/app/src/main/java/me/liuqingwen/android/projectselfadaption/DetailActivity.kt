package me.liuqingwen.android.projectselfadaption

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_activity_detail.*
import java.util.*

class DetailActivity : AppCompatActivity()
{
    companion object
    {
        private const val TITLE = "POST_TITLE"
        private const val CONTENT = "POST_CONTENT"
        private const val DATE = "POST_DATE"
        private const val AUTHOR = "POST_AUTHOR"
        private const val RATING = "POST_RATING"
        
        fun getIntent(context: Context, postTitle:String, postContent:String, postAuthor:String, postDate:Long, postRating:Float = 5.0f):Intent
        {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.TITLE, postTitle)
            intent.putExtra(DetailActivity.CONTENT, postContent)
            intent.putExtra(DetailActivity.DATE, postDate)
            intent.putExtra(DetailActivity.AUTHOR, postAuthor)
            intent.putExtra(DetailActivity.RATING, postRating)
            return intent
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_detail)
        
        this.init()
    }
    
    private fun init()
    {
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeButtonEnabled(true)
        
        val title = this.intent.getStringExtra(DetailActivity.TITLE)
        val content = this.intent.getStringExtra(DetailActivity.CONTENT)
        val date = Date(this.intent.getLongExtra(DetailActivity.DATE, Date().time))
        val author = this.intent.getStringExtra(DetailActivity.AUTHOR)
        val rating = this.intent.getFloatExtra(DetailActivity.RATING, 5.0f)
        
        val detailFragment = this.fragmentDetail as DetailFragment
        detailFragment.showPostDetail(Post(title, content, author, date, rating))
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if (item?.itemId == android.R.id.home)
        {
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
