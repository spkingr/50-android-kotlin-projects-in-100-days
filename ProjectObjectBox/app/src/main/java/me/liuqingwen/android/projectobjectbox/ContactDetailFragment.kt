package me.liuqingwen.android.projectobjectbox

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.RequestBuilder
import es.dmoral.toasty.Toasty
import io.objectbox.Box
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.guideline
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.find
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Qingwen on 2018-3-3, project: ProjectObjectBox.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-3
 * @Package: me.liuqingwen.android.projectobjectbox.view in project: ProjectObjectBox
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

@SuppressLint("CheckResult")
class ContactDetailFragment: BasicFragment(), AnkoLogger
{
    companion object
    {
        private const val PARAM_CONTACT = "contact"
        fun newInstance(contact: Contact) = ContactDetailFragment().apply { this.arguments = bundleOf(PARAM_CONTACT to contact) }
    }
    
    private var imageUrl: String = "http://liuqingwen.me/upload/images/android/zhuanjia.jpg"
    private lateinit var contactBox: Box<Contact>
    private lateinit var contact: Contact
    private val requestBuilder by lazy(LazyThreadSafetyMode.NONE) { super.getGlideRequest{
        this.placeholder(R.mipmap.ic_launcher)
        this.error(R.mipmap.ic_launcher)
        this.centerCrop()
    } }
    private val onImageSelectAction = { imageView:ImageView ->
        alert {
            lateinit var textImageUrl:EditText
            title = "Image URL"
            customView {
                textImageUrl = editText {
                    setText(this@ContactDetailFragment.imageUrl)
                }
            }
            negativeButton("Cancel"){}
            positiveButton("Done"){
                this@ContactDetailFragment.imageUrl = textImageUrl.text.toString()
                if (this@ContactDetailFragment.imageUrl.isNotBlank())
                {
                    this@ContactDetailFragment.requestBuilder?.load(this@ContactDetailFragment.imageUrl)?.into(imageView)
                }
            }
        }.show()
        Unit
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = ContactDetailUI(this.contact, this.onImageSelectAction, this.requestBuilder).createView(AnkoContext.create(this.ctx))
    
    fun saveData()
    {
        this.contactBox.put(this.contact)
        Toasty.success(this.ctx, "Save contact successfully!", Toast.LENGTH_SHORT, true).show()
    }
    
    /*private fun initContact()
    {
        val (_, name, phone, birthday, address, imageUrl, isStar, information) = this.contact
        
        if (imageUrl.isNotBlank())
        {
            this.requestBuilder?.load(imageUrl)?.into(this.find<ImageView>(ID_IMAGE_HEAD))
        }
        this.find<EditText>(ID_TEXT_NAME).setText(name)
        this.find<EditText>(ID_TEXT_PHONE).setText(phone)
        this.find<EditText>(ID_TEXT_BIRTHDAY).setText(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(birthday))
        this.find<EditText>(ID_TEXT_ADDRESS).setText(address)
        this.find<EditText>(ID_TEXT_INFO).setText(information ?: "No more information.")
        this.find<CheckedTextView>(ID_CHECKBOX_STAR).isChecked = isStar
    }*/
    
    fun updateContact()
    {
        val info = this.find<EditText>(ID_TEXT_INFO).text
        
        this.contact.name = this.find<EditText>(ID_TEXT_NAME).text.toString()
        this.contact.phone = this.find<EditText>(ID_TEXT_PHONE).text.toString()
        this.contact.birthday = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(this.find<EditText>(ID_TEXT_BIRTHDAY).text.toString()) ?: Date(0)
        this.contact.address = this.find<EditText>(ID_TEXT_ADDRESS).text.toString()
        this.contact.info = if (info.isBlank()) null else info.toString()  //copy always copy null to ""
        //this.contact.info = info.toString()
        this.contact.isStarContact = this.find<CheckedTextView>(ID_CHECKBOX_STAR).isChecked
        this.contact.profile = this.imageUrl
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.arguments?.let {
            this.contact = it.getParcelable(PARAM_CONTACT)
            this.imageUrl = this.contact.profile
        }
    }
    
    override fun onDestroy()
    {
        super.onDestroy()
        this.view?.contextJob?.cancel()
    }
    
    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        val app = context!!.applicationContext as MyApplication
        this.contactBox = app.objectBoxStore.boxFor(Contact::class.java)
    }
}

private var ID_LAYOUT_GUIDELINE = 0x11
private var ID_LAYOUT_CARD = 0x111
private var ID_IMAGE_HEAD = 0x12
private var ID_TEXT_NAME = 0x13
private var ID_LABEL_PHONE = 0x14
private var ID_LABEL_BIRTHDAY = 0x15
private var ID_LABEL_ADDRESS = 0x16
private var ID_LABEL_INFORMATION = 0x17
private var ID_TEXT_PHONE = 0x18
private var ID_TEXT_BIRTHDAY = 0x19
private var ID_TEXT_ADDRESS = 0x1A
private var ID_TEXT_INFO = 0x1B
private var ID_CHECKBOX_STAR = 0x1C
class ContactDetailUI(private val contact: Contact, private val onImageSelectAction: (ImageView)->Unit, private val request: RequestBuilder<Drawable>? = null) : AnkoComponent<Context>
{
    override fun createView(ui: AnkoContext<Context>) = with(ui) {
        constraintLayout {
            fitsSystemWindows = true
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            
            val outSize = Point().also { ui.ctx.windowManager.defaultDisplay.getSize(it) }
            val imageHeight = outSize.y / 3
            val (_, name, phone, birthday, address, imageUrl, isStar, information) = this@ContactDetailUI.contact
            
            guideline {
                id = ID_LAYOUT_GUIDELINE
            }.lparams(width = wrapContent, height = wrapContent)
            
            cardView {
                id = ID_LAYOUT_CARD
                radius = 32.0f
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    elevation = 8.0f
                }
                
                imageView {
                    id = ID_IMAGE_HEAD
                    imageResource = R.mipmap.ic_launcher
                    this@ContactDetailUI.request?.load(imageUrl)?.into(this)
                    setOnClickListener { this@ContactDetailUI.onImageSelectAction.invoke(this) }
                }.lparams(width = matchParent, height = matchParent)
            }.lparams(width = matchConstraint, height = imageHeight)
            
            
            editText(name) {
                id = ID_TEXT_NAME
                ems = 10
                hint = "Name"
                inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                textAlignment = EditText.TEXT_ALIGNMENT_CENTER
            }.lparams(width = wrapContent, height = wrapContent)
            
            textView("Phone Number:") {
                id = ID_LABEL_PHONE
            }.lparams(width = wrapContent, height = wrapContent)
            
            textView("Birthday:") {
                id = ID_LABEL_BIRTHDAY
            }.lparams(width = wrapContent, height = wrapContent)
            
            textView("Address:") {
                id = ID_LABEL_ADDRESS
            }.lparams(width = wrapContent, height = wrapContent)
            
            textView("Information:") {
                id = ID_LABEL_INFORMATION
            }.lparams(width = wrapContent, height = wrapContent)
            
            editText(phone) {
                id = ID_TEXT_PHONE
                ems = 10
                textSize = 14.0f
                inputType = InputType.TYPE_CLASS_PHONE
            }.lparams(width = matchConstraint, height = wrapContent)
            
            editText(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(birthday)) {
                id = ID_TEXT_BIRTHDAY
                ems = 10
                textSize = 14.0f
                inputType = InputType.TYPE_CLASS_DATETIME
                isClickable = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    focusable = View.FOCUSABLE
                }
            }.lparams(width = matchConstraint, height = wrapContent)
            
            editText(address) {
                id = ID_TEXT_ADDRESS
                ems = 10
                textSize = 14.0f
                inputType = InputType.TYPE_CLASS_TEXT
            }.lparams(width = matchConstraint, height = wrapContent)
            
            editText(information ?: "") {
                id = ID_TEXT_INFO
                ems = 10
                textSize = 14.0f
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }.lparams(width = matchConstraint, height = wrapContent)
            
            checkedTextView {
                id = ID_CHECKBOX_STAR
                checkMarkDrawableResource = R.drawable.ic_star_black_24dp
                isChecked = isStar
            }.lparams(width = wrapContent, height = wrapContent)
            
            /*constraintSet {
                create(ID_LAYOUT_GUIDELINE, VERTICAL_GUIDELINE)
            }*/
            
            applyConstraintSet {
                
                setGuidelinePercent(ID_LAYOUT_GUIDELINE, 0.3f)
                setDimensionRatio(ID_LAYOUT_CARD, "1:1")
                
                connect(
                        START of ID_LAYOUT_CARD to START of PARENT_ID margin dip(8),
                        END of ID_LAYOUT_CARD to END of PARENT_ID margin dip(8),
                        TOP of ID_LAYOUT_CARD to TOP of PARENT_ID,
                        BOTTOM of ID_LAYOUT_CARD to TOP of ID_LAYOUT_GUIDELINE,
        
                        START of ID_TEXT_NAME to START of PARENT_ID margin dip(8),
                        END of ID_TEXT_NAME to END of PARENT_ID margin dip(8),
                        TOP of ID_TEXT_NAME to BOTTOM of ID_LAYOUT_CARD margin dip(8),
        
                        START of ID_LABEL_PHONE to START of PARENT_ID margin dip(16),
                        TOP of ID_LABEL_PHONE to BOTTOM of ID_TEXT_NAME margin dip(16),
        
                        START of ID_LABEL_BIRTHDAY to START of ID_LABEL_PHONE,
                        TOP of ID_LABEL_BIRTHDAY to BOTTOM of ID_LABEL_PHONE margin dip(24),
        
                        START of ID_LABEL_ADDRESS to START of ID_LABEL_BIRTHDAY,
                        TOP of ID_LABEL_ADDRESS to BOTTOM of ID_LABEL_BIRTHDAY margin dip(24),
        
                        START of ID_LABEL_INFORMATION to START of ID_LABEL_ADDRESS,
                        TOP of ID_LABEL_INFORMATION to BOTTOM of ID_LABEL_ADDRESS margin dip(24),
        
                        START of ID_TEXT_PHONE to END of ID_LABEL_PHONE margin dip(8),
                        END of ID_TEXT_PHONE to END of PARENT_ID margin dip(8),
                        BASELINE of ID_TEXT_PHONE to BASELINE of ID_LABEL_PHONE,
        
                        START of ID_TEXT_BIRTHDAY to START of ID_TEXT_PHONE,
                        END of ID_TEXT_BIRTHDAY to END of PARENT_ID margin dip(8),
                        BASELINE of ID_TEXT_BIRTHDAY to BASELINE of ID_LABEL_BIRTHDAY,
        
                        START of ID_TEXT_ADDRESS to START of ID_TEXT_BIRTHDAY,
                        END of ID_TEXT_ADDRESS to END of PARENT_ID margin dip(8),
                        BASELINE of ID_TEXT_ADDRESS to BASELINE of ID_LABEL_ADDRESS,
        
                        START of ID_TEXT_INFO to START of ID_TEXT_ADDRESS,
                        END of ID_TEXT_INFO to END of PARENT_ID margin dip(8),
                        TOP of ID_TEXT_INFO to BOTTOM of ID_TEXT_ADDRESS margin dip(8),
        
                        END of ID_CHECKBOX_STAR to END of PARENT_ID margin dip(8),
                        TOP of ID_CHECKBOX_STAR to TOP of PARENT_ID
                       )
            }
        }
    }
}