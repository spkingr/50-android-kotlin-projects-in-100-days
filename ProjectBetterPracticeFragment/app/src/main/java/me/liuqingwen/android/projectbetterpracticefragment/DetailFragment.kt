package me.liuqingwen.android.projectbetterpracticefragment

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_fragment_detail.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment()
{
    companion object
    {
        private val PARAM_CONTACT = "contact"
        fun newInstance(contact: Contact): DetailFragment
        {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putParcelable(PARAM_CONTACT, contact)
            fragment.arguments = args
            return fragment
        }
    }
    private var activityContext : Context? = null
    private lateinit var contact:Contact

    private fun restoreDefaultInfo()
    {
        this.textName.text = this.contact.name.toEditable()
        this.textPhone.text = this.contact.phone.toEditable()
        this.textBirthday.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this.contact.birthday).toEditable()
        this.textAddress.text = this.contact.address.toEditable()
        this.textInfo.text = this.contact.info.toEditable()
        this.checkedStar.isChecked = this.contact.isStarContact
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            this.checkedStar.checkMarkTintList = ColorStateList.valueOf(if (this.checkedStar.isChecked) Color.RED else Color.DKGRAY)
        }
    }
    
    private fun saveNewData()
    {
        with(this.contact)
        {
            this.name = this@DetailFragment.textName.text.toString()
            this.phone = this@DetailFragment.textPhone.text.toString()
            this.birthday = java.text.SimpleDateFormat("yyyy-MM-dd",
                                                       java.util.Locale.getDefault()).parse(this@DetailFragment.textBirthday.text.toString()).time
            this.address = this@DetailFragment.textAddress.text.toString()
            this.info = this@DetailFragment.textInfo.text.toString()
            this.isStarContact = this@DetailFragment.checkedStar.isChecked
        }
        this.activityContext?.let { DatabaseHelper.getInstance(it).modifyContacts(this.contact) }
    }
    
    private fun isChanged() = (this.textName.text.toString() != this.contact.name || this.textPhone.text.toString() != this.contact.phone
                                || this.textBirthday.text.toString() != SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            Date(this.contact.birthday))
                                || this.textAddress.text.toString() != this.contact.address || this.textInfo.text.toString() != this.contact.info
                                || this.checkedStar.isChecked!= this.contact.isStarContact)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (arguments != null)
        {
            this.contact = arguments!!.getParcelable(PARAM_CONTACT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.layout_fragment_detail, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        
        this.checkedStar.setOnClickListener {
            this.checkedStar.isChecked = ! this.checkedStar.isChecked
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                this.checkedStar.checkMarkTintList = ColorStateList.valueOf(if (this.checkedStar.isChecked) Color.RED else Color.DKGRAY)
            }
        }
        this.restoreDefaultInfo()
    }
    
    override fun onAttach(context: Context?)
    {
        this.activityContext = context!!
        super.onAttach(context)
    }
    
    override fun onDetach()
    {
        this.activityContext = null
        super.onDetach()
    }

}
