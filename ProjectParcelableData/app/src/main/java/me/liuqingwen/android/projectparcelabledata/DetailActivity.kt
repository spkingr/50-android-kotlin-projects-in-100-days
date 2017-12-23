package me.liuqingwen.android.projectparcelabledata

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_activity_detail.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity()
{
    companion object
    {
        private const val DATA_NAME = "contact_data"
        fun getDetailIntent(context: Context, contact: Contact) = Intent(context, DetailActivity::class.java).apply { this.putExtra(DetailActivity.DATA_NAME, contact) }
    }
    
    private lateinit var contact:Contact
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_detail)
        
        this.init()
    }
    
    private fun init()
    {
        this.contact = this.intent.getParcelableExtra<Contact>(DetailActivity.DATA_NAME)
        
        this.setSupportActionBar(this.toolbar)
        this.supportActionBar?.title = "Contact Information"
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        this.buttonRestore.setOnClickListener {
            this.restoreDefaultInfo()
        }
        
        this.buttonSave.setOnClickListener {
            if (this.isChanged())
            {
                this.saveNewData()
                toast("Modification saved successfully!")
                this.finish()
            }
            else
            {
                toast("No changes should be saved.")
            }
        }
        
        this.restoreDefaultInfo()
    }
    
    private fun restoreDefaultInfo()
    {
        this.textName.text = this.contact.name.toEditable()
        this.textPhone.text = this.contact.phone.toEditable()
        this.textBirthday.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this.contact.birthday).toEditable()
        this.textAddress.text = this.contact.address.toEditable()
        this.textInfo.text = this.contact.info.toEditable()
        
        this.ratingStar.rating = if (this.contact.isStarContact) 1.0f else 0.0f
    }
    
    private fun saveNewData()
    {
        with(this.contact)
        {
            this.name = this@DetailActivity.textName.text.toString()
            this.phone = this@DetailActivity.textPhone.text.toString()
            this.birthday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this@DetailActivity.textBirthday.text.toString()).time
            this.address = this@DetailActivity.textAddress.text.toString()
            this.info = this@DetailActivity.textInfo.text.toString()
            this.isStarContact = this@DetailActivity.ratingStar.rating > 0.5f
        }
        DatabaseHelper.getInstance(this).modifyContacts(this.contact)
    }
    
    private fun isChanged() =  (this.textName.text.toString() != this.contact.name || this.textPhone.text.toString() != this.contact.phone
            || this.textBirthday.text.toString() != SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(this.contact.birthday))
            || this.textAddress.text.toString() != this.contact.address || this.textInfo.text.toString() != this.contact.info
            || this.ratingStar.rating.toInt() != if(this.contact.isStarContact) 1 else 0 )
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            android.R.id.home -> {
                this.onBackPressed()
            }
            else -> {
                toast("Not implemented yet!")
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onBackPressed()
    {
        if (this.isChanged())
        {
            alert {
                title = "Quit without save?"
                negativeButton("Quit") {
                    super.onBackPressed()
                }
                positiveButton("Stay") {}
            }.show()
        }
        else
        {
            super.onBackPressed()
        }
    }
}
