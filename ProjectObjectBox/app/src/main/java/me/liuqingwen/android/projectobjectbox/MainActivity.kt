package me.liuqingwen.android.projectobjectbox

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import kotlinx.coroutines.experimental.Job
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton

class MainActivity : AppCompatActivity(), ContactListFragment.IFragmentInteractionListener
{
    companion object
    {
        private const val REQUEST_CONTACT_CODE = 100
        const val REQUEST_CONTACT_EXTRA = "contact_id"
    }
    override val job: Job get() = Job()
    private lateinit var fragmentList: ContactListFragment
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        MainUI().setContentView(this)
        
        this.init()
    }
    
    private fun init()
    {
        this.setupView()
        
        this.fragmentList = ContactListFragment.newInstance()
        this.supportFragmentManager.beginTransaction().replace(ID_LAYOUT_CONTAINER, fragmentList).commit()
    }
    
    private fun setupView()
    {
        val toolbar = this.find<Toolbar>(ID_LAYOUT_TOOLBAR)
        this.setSupportActionBar(toolbar)
        this.supportActionBar?.title = "Contact List"
        
        val buttonAdd = this.find<FloatingActionButton>(
                ID_BUTTON_ADD)
        buttonAdd.setOnClickListener {
            val intent = DetailActivity.getIntent(this, null)
            this.startActivityForResult(intent, MainActivity.REQUEST_CONTACT_CODE)
        }
    }
    
    override fun onContactSelect(contact: Contact)
    {
        val intent = DetailActivity.getIntent(this, contact)
        this.startActivityForResult(intent, MainActivity.REQUEST_CONTACT_CODE)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MainActivity.REQUEST_CONTACT_CODE && data != null)
        {
            this.fragmentList.updateOrInsert(data.getLongExtra(MainActivity.REQUEST_CONTACT_EXTRA, 0L))
        }
    }
    
    override fun onDestroy()
    {
        super.onDestroy()
        this.job.cancel()
    }
}

private var ID_LAYOUT_CONTAINER = 0x01
private var ID_LAYOUT_TOOLBAR = 0x02
private var ID_BUTTON_ADD = 0x03
class MainUI:AnkoComponent<Activity>
{
    override fun createView(ui: AnkoContext<Activity>) = with(ui) {
        coordinatorLayout {
            fitsSystemWindows = true
            
            appBarLayout {
                toolbar {
                    id = ID_LAYOUT_TOOLBAR
                }.lparams(width = matchParent, height = dip(56))
            }.lparams(width = matchParent, height = wrapContent)
            
            frameLayout {
                id = ID_LAYOUT_CONTAINER
            }.lparams(width = matchParent, height = matchParent){
                behavior = AppBarLayout.ScrollingViewBehavior() //important!!!
            }
            
            floatingActionButton {
                id = ID_BUTTON_ADD
                imageResource = R.drawable.ic_add_black_24dp
                colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }.lparams{
                gravity = Gravity.END or Gravity.BOTTOM
                marginEnd = dip(8)
                bottomMargin = dip(8)
            }
        }
    }
}