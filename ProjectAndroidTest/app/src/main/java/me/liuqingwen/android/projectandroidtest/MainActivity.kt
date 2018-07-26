package me.liuqingwen.android.projectandroidtest

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.Contacts
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.navigation_header.view.*
import me.liuqingwen.android.projectandroidtest.addnote.AddNoteFragment
import me.liuqingwen.android.projectandroidtest.notedetail.NoteDetailFragment
import me.liuqingwen.android.projectandroidtest.noteslist.NotesListFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.navigationView
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.UI
import org.jetbrains.anko.constraint.layout.matchConstraint

class MainActivity : AppCompatActivity(), AnkoLogger,
                     NotesListFragment.OnFragmentInteractionListener, NoteDetailFragment.OnFragmentInteractionListener, AddNoteFragment.OnFragmentInteractionListener
{
    private val fragmentNotesList by lazy { NotesListFragment.newInstance() }
    private val isHomeFragment get() = this.supportFragmentManager.backStackEntryCount == 0
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        MainUI().setContentView(this)
        
        this.init()
    }
    
    private fun init()
    {
        val toolbar = this.find<Toolbar>(MainUI.ID_TOOL_BAR)
        this.setSupportActionBar(toolbar)
        
        this.supportFragmentManager.addOnBackStackChangedListener {
            if (this.isHomeFragment)
            {
                this.setTitle(NotesListFragment.TITLE)
                this.configFloatingButton(FloatingButtonType.ADD, true) { this.navigateToAddNote() }
            }
        }
        
        this.supportFragmentManager.beginTransaction()
                .replace(MainUI.ID_LAYOUT_CONTAINER, this.fragmentNotesList)
                .commit()
    }
    
    override fun setTitle(title: String)
    {
        this.supportActionBar?.title = title
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (this.isHomeFragment){
            this.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }else{
            val typedValue = TypedValue()
            this.theme.resolveAttribute(android.R.attr.homeAsUpIndicator, typedValue, true)
            this.supportActionBar?.setHomeAsUpIndicator(typedValue.resourceId)
        }
    }
    
    override fun configFloatingButton(buttonType: FloatingButtonType, isVisible: Boolean, onClickListener: ((View) -> Unit)?)
    {
        val buttonFloating = this.find<FloatingActionButton>(MainUI.ID_BUTTON_FLOATING)
        if (buttonType == FloatingButtonType.NONE)
        {
            buttonFloating.visibility = View.GONE
            return
        }
    
        buttonFloating.imageResource = buttonType.srcCompat
        buttonFloating.backgroundTintList = ColorStateList.valueOf(this.resources.getColor(buttonType.backgroundTint))
        buttonFloating.visibility = if (isVisible) View.VISIBLE else View.GONE
        onClickListener?.let {
            buttonFloating.setOnClickListener(it)
        }
    }
    
    override fun navigateToAddNote() = this.navigateToFragment(AddNoteFragment.newInstance())
    
    override fun navigateToNoteDetail(noteId:Int) = this.navigateToFragment(NoteDetailFragment.newInstance(noteId))
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            android.R.id.home -> {
                if (this.isHomeFragment){
                    val layoutDrawer = this.find<DrawerLayout>(MainUI.ID_LAYOUT_DRAWER)
                    layoutDrawer.openDrawer(Gravity.START)
                }else{
                    super.onBackPressed()
                }
                return true
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun navigateToFragment(newFragment:Fragment, name:String? = null)
    {
        this.supportFragmentManager
                .beginTransaction()
                .hide(this.fragmentNotesList)
                .add(MainUI.ID_LAYOUT_CONTAINER, newFragment)
                .addToBackStack(name)
                .commit()
    }
    
}


class MainUI:AnkoComponent<MainActivity>{
    companion object
    {
        const val ID_LAYOUT_DRAWER = 0x101
        const val ID_TOOL_BAR = 0x102
        const val ID_LAYOUT_CONTAINER = 0x103
        const val ID_BUTTON_FLOATING = 0x104
        const val ID_NAVIGATION_VIEW = 0x105
    
        const val ID_IMAGE_NAVIGATOR = 0x111
        const val ID_LABEL_HEADER = 0x112
    }
    
    override fun createView(ui: AnkoContext<MainActivity>) = with(ui){
        drawerLayout {
            id = ID_LAYOUT_DRAWER
            fitsSystemWindows = true
    
            coordinatorLayout {
                appBarLayout {
                    toolbar {
                        id = ID_TOOL_BAR
                    }.lparams(width = matchParent, height = wrapContent)
                }.lparams(width = matchParent, height = wrapContent)
        
                frameLayout {
                    id = ID_LAYOUT_CONTAINER
                    fitsSystemWindows = true
                }.lparams(width = matchParent, height = matchParent){
                    behavior = AppBarLayout.ScrollingViewBehavior()
                }
        
                floatingActionButton {
                    id = ID_BUTTON_FLOATING
                    imageResource = R.drawable.ic_add_black_24dp
                    backgroundColor = R.color.colorAccent
                }.lparams(width = wrapContent, height = wrapContent){
                    bottomMargin = dip(8)
                    marginEnd = dip(8)
                    anchorId = ID_LAYOUT_CONTAINER
                    anchorGravity = Gravity.BOTTOM or Gravity.END
                    behavior = AppBarLayout.ScrollingViewBehavior()
                }
            }.lparams(width = matchParent, height = matchParent)
    
            navigationView {
                id = ID_NAVIGATION_VIEW
                fitsSystemWindows = true
        
                addHeaderView(ctx.UI {
                    constraintLayout {
                        imageView {
                            id = ID_IMAGE_NAVIGATOR
                            imageResource = R.mipmap.ic_launcher_round
                            scaleType = ImageView.ScaleType.CENTER
                        }.lparams(width = wrapContent, height = wrapContent)
        
                        textView("Header Title") {
                            id = MainUI.ID_LABEL_HEADER
                        }.lparams(width = wrapContent, height = wrapContent)
        
                        applyConstraintSet {
                            connect(
                                    TOP of ID_IMAGE_NAVIGATOR to TOP of PARENT_ID margin dip(8),
                                    START of ID_IMAGE_NAVIGATOR to START of PARENT_ID margin dip(8),
                                    END of ID_IMAGE_NAVIGATOR to END of PARENT_ID margin dip(8),
                    
                                    TOP of ID_LABEL_HEADER to BOTTOM of ID_IMAGE_NAVIGATOR margin dip(8),
                                    START of ID_LABEL_HEADER to START of PARENT_ID margin dip(8),
                                    END of ID_LABEL_HEADER to END of PARENT_ID margin dip(8)
                                   )
                        }
                    }
                }.view)
        
                inflateMenu(R.menu.navigation_menu)
            }.lparams(width = wrapContent, height = matchParent, gravity = Gravity.START)
        }
    }
}