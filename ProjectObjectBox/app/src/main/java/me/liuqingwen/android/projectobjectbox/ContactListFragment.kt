package me.liuqingwen.android.projectobjectbox

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import es.dmoral.toasty.Toasty
import io.objectbox.Box
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Qingwen on 2018-3-2, project: ProjectObjectBox.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-2
 * @Package: me.liuqingwen.android.projectobjectbox.view in project: ProjectObjectBox
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

@SuppressLint("CheckResult")
class ContactListFragment: BasicFragment(), AnkoLogger
{
    interface IFragmentInteractionListener:IJobHolder
    {
        fun onContactSelect(contact: Contact)
    }
    
    companion object
    {
        fun newInstance() = ContactListFragment()
    }
    
    private var fragmentInteractionListener: IFragmentInteractionListener? = null
    private lateinit var contactBox: Box<Contact>
    private lateinit var layoutSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private val contactList by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<Contact>() }
    private val adapter by lazy(LazyThreadSafetyMode.NONE) {ContactListAdapter(this.contactList,super.getGlideRequest {
        this.placeholder(R.mipmap.ic_launcher_round)
        this.error(R.mipmap.ic_launcher_round)
        this.circleCrop() }, { this.updateData(it) }, { this.selectContact(it) }, { this.popUpMenuFor(it) })
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = UI {
        layoutSwipeRefreshLayout = swipeRefreshLayout {
            onRefresh {
                launch(contextJob + UI) {
                    loadData()
                    isRefreshing = false
                }
            }
            recyclerView = recyclerView {
                adapter = this@ContactListFragment.adapter
                layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            }
        }
    }.view
    
    fun updateOrInsert(id: Long)
    {
        val contact = this.contactBox.get(id)
        if (id > 0)
        {
            val index = this.contactList.withIndex().find { it.value.id == id }?.index
            if (index != null)
            {
                this.contactList[index] = contact
                this.adapter.notifyItemChanged(index)
            }
            else
            {
                this.contactList.add(contact)
                this.adapter.notifyItemInserted(this.contactList.size - 1)
            }
        }
    }
    
    private suspend fun loadData()
    {
        val listCount = this.contactList.size.toLong()
        val dataCount = this.contactBox.count()
        if (dataCount == listCount && this.contactBox.all.containsAll(this.contactList))
        {
            this.toast("")
            if (dataCount <= 0) Toasty.warning(this.ctx,"Contact list is empty!", Toast.LENGTH_SHORT, true).show() else Toasty.info(this.ctx,"Already the newest list.", Toast.LENGTH_SHORT, true).show()
        }
        else
        {
            this.contactList.clear()
            this.contactList += this.contactBox.all
            if (listCount >= 0)
            {
                this.adapter.notifyDataSetChanged()
            }
            
            Toasty.success(this.ctx, "Contact list refreshed!", Toast.LENGTH_SHORT, true).show()
        }
        
        //Simulate the http or io processing
        delay(1000)
    }
    
    private suspend fun updateData(contact: Contact)
    {
        this.contactBox.put(contact)
        
        //Simulate the http or io processing
        delay(500)
        
        Toasty.success(this.ctx, "Contact saved successfully!", Toast.LENGTH_SHORT, true).show()
    }
    
    private suspend fun saveData(contact: Contact, position: Int)
    {
        this.updateData(contact)
        
        this.contactList.add(position, contact)
        this.adapter.notifyItemInserted(position)
    }
    
    private fun selectContact(contact: Contact)
    {
        this.fragmentInteractionListener?.onContactSelect(contact)
    }
    
    private fun popUpMenuFor(contact: Contact)
    {
        val position = contactList.indexOf(contact)
        selector("[ ${contact.name} ]", listOf("Contact Information", "Copy As New", "Delete Contact")){_, selection ->
            when(selection)
            {
                0 -> {
                    this.selectContact(contact)
                }
                1 -> {
                    val newContact = contact.copy(id = 0)
                    this.contactBox.put(newContact)
                    this.contactList.add(newContact)
                    this.adapter.notifyItemInserted(this.contactList.size - 1)
                }
                2 -> {
                    alert("Are your sure DELETE [ ${contact.name} ] from your contact list? The action cannot be restored!", "Warning") {
                        positiveButton("Cancel"){}
                        negativeButton("Delete"){
                            this@ContactListFragment.contactBox.remove(contact.id)
                            this@ContactListFragment.contactList.removeAt(position)
                            this@ContactListFragment.adapter.notifyItemRemoved(position)
    
                            val view = this@ContactListFragment.recyclerView
                            snackbar(view, "Mis-operation?", "Undo"){
                                launch(view.contextJob + UI) {
                                    this@ContactListFragment.saveData(contact, position)
                                }
                            }
                        }
                    }.show()
                }
                else -> Unit
            }
        }
    }
    
    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        
        val app = context!!.applicationContext as MyApplication
        this.contactBox = app.objectBoxStore.boxFor(Contact::class.java)
        this.fragmentInteractionListener = context as? IFragmentInteractionListener
    
        //I can load data before view created, because I just load the data without refresh the list!
        launch(context.contextJob + UI) {
            this@ContactListFragment.loadData()
        }
    }
    
    override fun onDestroy()
    {
        super.onDestroy()
        this.fragmentInteractionListener?.job?.cancel()
    }
}

/**
 * Here is the bug after the list scroll, the frist click will not be responsed!
 * And the issue is opened here: [https://issuetracker.google.com/issues/66996774?pli=1]
 */
class ContactViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
{
    fun bind(contact: Contact, requestBuilder: RequestBuilder<Drawable>?, actionCheck: (suspend (Contact) -> Unit)?, actionClick: ((Contact) -> Unit)?, actionLongClick: ((Contact) -> Unit)?)
    {
        val (_, name, _, birthday, address, imageUrl, isStar, _) = contact
        
        this.itemView.find<TextView>(ID_LABEL_NAME).text = name
        this.itemView.find<TextView>(
                ID_LABEL_BIRTHDAY).text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(birthday)
        this.itemView.find<TextView>(ID_LABEL_ADDRESS).text = address
        
        val checkboxStar = this.itemView.find<CheckBox>(
                ID_CHECKBOX_STAR)
        checkboxStar.isChecked = isStar
        checkboxStar.setOnCheckedChangeListener { checkBox, isChecked ->
            contact.isStarContact = isChecked
            checkBox.isEnabled = false
            launch(UI) {
                actionCheck?.invoke(contact)
                checkBox.isEnabled = true
            }
        }
        
        val imageHead = this.itemView.find<ImageView>(
                ID_IMAGE_HEAD)
        requestBuilder?.load(imageUrl)?.into(imageHead)
        
        actionClick?.run { this@ContactViewHolder.itemView.setOnClickListener { this.invoke(contact) } }
        actionLongClick?.run { this@ContactViewHolder.itemView.setOnLongClickListener { this.invoke(contact); true } }
    }
}

class ContactListAdapter(private val contactList: List<Contact>, private val requestBuilder: RequestBuilder<Drawable>? = null, val onItemCheckListener: (suspend (Contact) -> Unit)? = null, val onItemClickListener: ((Contact) -> Unit)? = null, val onItemLongClickListener: ((Contact) -> Unit)? = null):
        RecyclerView.Adapter<ContactViewHolder>(), ListPreloader.PreloadModelProvider<Contact>
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContactViewHolder(
            ContactListUI().createView(
                    AnkoContext.create(parent.context, parent)))
    override fun getItemCount() = this.contactList.size
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) = holder.bind(this.contactList[position], this.requestBuilder, this.onItemCheckListener, this.onItemClickListener, this.onItemLongClickListener)
    override fun getPreloadItems(position: Int) = this.contactList.slice(position until position + 1)
    override fun getPreloadRequestBuilder(item: Contact) = this.requestBuilder
}

private var ID_IMAGE_HEAD = 0x01
private var ID_LABEL_NAME = 0x02
private var ID_LABEL_BIRTHDAY = 0x03
private var ID_LABEL_ADDRESS = 0x04
private var ID_CHECKBOX_STAR = 0x05
class ContactListUI : AnkoComponent<ViewGroup>
{
    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        constraintLayout {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            
            imageView {
                id = ID_IMAGE_HEAD
            }.lparams(width = matchConstraint, height = dip(80))
        
            textView {
                id = ID_LABEL_NAME
                typeface = Typeface.DEFAULT_BOLD
            }.lparams(width = wrapContent, height = wrapContent)
        
            textView {
                id = ID_LABEL_BIRTHDAY
                textSize = 12.0f
            }.lparams(width = wrapContent, height = wrapContent)
        
            textView {
                id = ID_LABEL_ADDRESS
                textSize = 12.0f
            }.lparams(width = matchConstraint, height = matchConstraint)
        
            checkBox {
                id = ID_CHECKBOX_STAR
            }.lparams(width = wrapContent, height = wrapContent)
            
            applyConstraintSet {
            
                setDimensionRatio(ID_IMAGE_HEAD, "w,1:1")
            
                connect(
                        START of ID_IMAGE_HEAD to START of PARENT_ID margin dip(8),
                        TOP of ID_IMAGE_HEAD to TOP of PARENT_ID margin dip(4),
                        BOTTOM of ID_IMAGE_HEAD to BOTTOM of PARENT_ID margin dip(4),
        
                        START of ID_LABEL_NAME to END of ID_IMAGE_HEAD margin dip(8),
                        TOP of ID_LABEL_NAME to TOP of PARENT_ID margin dip(4),
        
                        START of ID_LABEL_BIRTHDAY to START of ID_LABEL_NAME,
                        TOP of ID_LABEL_BIRTHDAY to BOTTOM of ID_LABEL_NAME margin dip(8),
        
                        START of ID_LABEL_ADDRESS to START of ID_LABEL_NAME,
                        END of ID_LABEL_ADDRESS to START of ID_CHECKBOX_STAR margin dip(8),
                        TOP of ID_LABEL_ADDRESS to BOTTOM of ID_LABEL_BIRTHDAY margin dip(4),
                        BOTTOM of ID_LABEL_ADDRESS to BOTTOM of PARENT_ID,
        
                        END of ID_CHECKBOX_STAR to END of PARENT_ID margin dip(8),
                        TOP of ID_CHECKBOX_STAR to TOP of PARENT_ID margin dip(8),
                        BOTTOM of ID_CHECKBOX_STAR to BOTTOM of PARENT_ID margin dip(8)
                       )
            }
        }
    }
}