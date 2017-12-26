package me.liuqingwen.android.projectphotowall

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_activity_detail.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity()
{
    companion object
    {
        private const val IS_VIEW_NOT_ADD = "is_view_not_add"
        private const val PHOTO_DATA = "photo_data"
        fun getIntent(context:Context, isView:Boolean = true, photo:Photo? = null)  = Intent(context, DetailActivity::class.java).apply {
            this.putExtra(DetailActivity.IS_VIEW_NOT_ADD, isView)
            photo?.let { this.putExtra(DetailActivity.PHOTO_DATA, it) }
        }
    }
    
    private var isViewNotAdd:Boolean = true
    private lateinit var photo:Photo
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_detail)
        
        this.init()
    }
    
    private fun init()
    {
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        this.isViewNotAdd = this.intent.getBooleanExtra(DetailActivity.IS_VIEW_NOT_ADD, false)
        if (this.isViewNotAdd)
        {
            this.supportActionBar?.title = "View Photo"
            this.photo = this.intent.getParcelableExtra(DetailActivity.PHOTO_DATA)
            
            this.textUrl.text = this.photo.url.toEditable()
            this.textAuthor.text = this.photo.author.toEditable()
            this.textDescription.text = this.photo.description.toEditable()
            this.photo.date?.let { this.labelDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) }
            
            //load image
            displayImageFromUrl(this, this.photo.url, this.imageView)
            
            this.buttonSave.text = this.getString(R.string.update_and_return)
        }
        else
        {
            this.supportActionBar?.title = "Add New"
            this.buttonSave.text = this.getString(R.string.save_and_return)
        }
        
        this.buttonView.setOnClickListener {
            displayImageFromUrl(this, this.textUrl.text.toString(), this.imageView)
        }
        this.buttonSave.setOnClickListener {
            if (this.isViewNotAdd)
            {
                val isUrlUpdated = this.photo.url != this.textUrl.text.toString()
                this.photo.url = this.textUrl.text.toString()
                this.photo.author = this.textAuthor.text.toString()
                this.photo.date = Date()
                this.photo.description = this.textDescription.text.toString()
                
                doAsync {
                    AppDatabaseHelper.getInstance(this@DetailActivity).updatePhotos(this@DetailActivity.photo)
                    uiThread {
                        if (isUrlUpdated)
                        {
                            this@DetailActivity.setResult(AppCompatActivity.RESULT_OK)
                        }
                        this@DetailActivity.finish()
                    }
                }
            }
            else
            {
                val photo = Photo()
                photo.url = this.textUrl.text.toString()
                photo.author = this.textAuthor.text.toString()
                photo.date = Date()
                photo.description = this.textDescription.text.toString()
                
                doAsync {
                    AppDatabaseHelper.getInstance(this@DetailActivity).addPhotos(photo)
                    uiThread {
                        this@DetailActivity.setResult(AppCompatActivity.RESULT_OK)
                        this@DetailActivity.finish()
                    }
                }
            }
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            android.R.id.home -> { this.onBackPressed() }
            else -> {this.toast("Not implemented yet!")}
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onBackPressed()
    {
        if ((this.isViewNotAdd && this.textUrl.text.toString() != this.photo.url) || (! this.isViewNotAdd && this.textUrl.text.isNotEmpty()))
        {
            this.alert {
                this.title = "Warning"
                this.message = "Are you sure to leave without saving?"
                negativeButton("Quit"){ super.onBackPressed() }
                positiveButton("Stay"){  }
            }.show()
        }
        else
        {
            super.onBackPressed()
        }
    }
}
