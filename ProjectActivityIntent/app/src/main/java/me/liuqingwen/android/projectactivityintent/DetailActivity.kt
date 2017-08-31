package me.liuqingwen.android.projectactivityintent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_activity_detail.*

class DetailActivity : AppCompatActivity()
{
    
    companion object
    {
        private const val IMAGE_URL = "imageUrl"
        private const val IMAGE_DES = "imageDescription"
        private const val TITLE = "title"
        fun getDetailIntent(context:Context, imageUrl:String, imageDescription:String, title:String):Intent
        {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.IMAGE_URL, imageUrl)
            intent.putExtra(DetailActivity.IMAGE_DES, imageDescription)
            intent.putExtra(DetailActivity.TITLE, title)
            return intent
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_detail)
        
        this.init()
        this.display()
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if (item?.itemId == android.R.id.home)
        {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun display()
    {
        val imageUrl = this.intent.getStringExtra(DetailActivity.IMAGE_URL)
        val imageDes = this.intent.getStringExtra(DetailActivity.IMAGE_DES)
        Glide.with(this).load(imageUrl).into(this.imageDetail)
        this.labelDetail.text = imageDes
        
        this.supportActionBar?.title = this.intent.getStringExtra(DetailActivity.TITLE)
    }
    
    private fun init()
    {
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.left)
        
        this.buttonDelete.setOnClickListener {
            Snackbar.make(it, "Delete the item?", Snackbar.LENGTH_LONG).setAction("Delete"){
                this.setResult(AppCompatActivity.RESULT_OK)
                this.finish()
            }.show()
        }
    }
    
}
