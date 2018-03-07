package me.liuqingwen.android.projectobjectbox

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MenuItem
import kotlinx.coroutines.experimental.Job
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.support.v4.nestedScrollView
import java.util.*

class DetailActivity : AppCompatActivity(), IJobHolder
{
    companion object
    {
        private const val INTENT_CONTACT = "contact"
        fun getIntent(activity: Context, contact: Contact?) = Intent(activity, DetailActivity::class.java).apply {  this.putExtra(INTENT_CONTACT, contact) }
    }
    private lateinit var contact: Contact
    private lateinit var contactClone: Contact
    private lateinit var fragmentDetail: ContactDetailFragment
    private var isSaved = false
    
    override val job: Job get() = Job()
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        DetailUI().setContentView(this)
        
        this.init()
    }
    
    private fun init()
    {
        this.contact = this.intent.getParcelableExtra(INTENT_CONTACT) ?: Contact(0, "", "", Date(0), "", "")
        this.contactClone = this.contact.copy()
        this.setupView()
        
        this.fragmentDetail = ContactDetailFragment.newInstance(this.contact)
        this.supportFragmentManager.beginTransaction().replace(ID_LAYOUT_CONTAINER, this.fragmentDetail).commit()
    }
    
    private fun setupView()
    {
        val toolbar = this.find<Toolbar>(ID_LAYOUT_TOOLBAR)
        this.setSupportActionBar(toolbar)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        this.supportActionBar?.title = this.contact.name
        
        val buttonSave = this.find<FloatingActionButton>(ID_BUTTON_ADD)
        buttonSave.setOnClickListener {
            this.fragmentDetail.updateContact()
            this.fragmentDetail.saveData()
            this.isSaved = true
        }
    }
    
    override fun onBackPressed()
    {
        if (this.isSaved)
        {
            this.setResult(Activity.RESULT_OK, Intent().also { it.putExtra(MainActivity.REQUEST_CONTACT_EXTRA, this.contact.id) })
            super.onBackPressed()
        }
        else
        {
            this.fragmentDetail.updateContact()
            if (this.contact != this.contactClone)
            {
                alert("Contact information has changed, discard changes or save before leaving?", "Notice") {
                    negativeButton("Stay"){}
                    positiveButton("Discard"){
                        this@DetailActivity.setResult(Activity.RESULT_CANCELED)
                        super.onBackPressed()
                    }
                }.show()
            }
            else
            {
                super.onBackPressed()
            }
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if (item?.itemId == android.R.id.home)
        {
            this.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onDestroy()
    {
        super.onDestroy()
        this.job.cancel()
    }
}

private var ID_LAYOUT_CONTAINER = 0x30
private var ID_LAYOUT_TOOLBAR = 0x31
private var ID_BUTTON_ADD = 0x32
class DetailUI: AnkoComponent<Activity>
{
    override fun createView(ui: AnkoContext<Activity>) = with(ui) {
        coordinatorLayout {
            fitsSystemWindows = true
            
            appBarLayout {
                toolbar {
                    id = ID_LAYOUT_TOOLBAR
                }.lparams(width = matchParent, height = dip(56))
            }.lparams(width = matchParent, height = wrapContent)
            
            nestedScrollView {
                frameLayout {
                    id = ID_LAYOUT_CONTAINER
                }.lparams(width = matchParent, height = matchParent)
            }.lparams(width = matchParent, height = matchParent){
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
            
            floatingActionButton {
                id = ID_BUTTON_ADD
                imageResource = R.drawable.ic_check_black_24dp
                colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }.lparams{
                gravity = Gravity.END or Gravity.BOTTOM
                marginEnd = dip(8)
                bottomMargin = dip(8)
            }
        }
    }
}