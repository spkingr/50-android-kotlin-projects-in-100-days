package me.liuqingwen.android.projectandroidtest.addnote

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import me.liuqingwen.android.projectandroidtest.FloatingButtonType
import me.liuqingwen.android.projectandroidtest.IMainInteractionListener
import me.liuqingwen.android.projectandroidtest.R
import me.liuqingwen.android.projectandroidtest.data.InMemoryNotesServiceApi
import me.liuqingwen.android.projectandroidtest.data.Note
import me.liuqingwen.android.projectandroidtest.data.NotesRepositories
import me.liuqingwen.android.projectandroidtest.util.MyGlideEngine
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.toast
import pub.devrel.easypermissions.EasyPermissions

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddNoteFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddNoteFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AddNoteFragment : Fragment(), IAddNoteContract.IView, EasyPermissions.PermissionCallbacks, AnkoLogger
{
    interface OnFragmentInteractionListener : IMainInteractionListener
    
    companion object
    {
        fun newInstance() = AddNoteFragment()
        private const val TITLE = "Add Note"
        private const val REQUEST_RESULT = 100
        private const val REQUEST_CODE = 200
    }
    
    private var listener: OnFragmentInteractionListener? = null
    private val repository by lazy(LazyThreadSafetyMode.NONE) { NotesRepositories.getRepository(InMemoryNotesServiceApi()) }
    private val presenter by lazy(LazyThreadSafetyMode.NONE) { AddNotePresenter(this.repository, this) }
    private val progressBar by lazy(LazyThreadSafetyMode.NONE) { indeterminateProgressDialog("Saving, please wait...", "Save")}
    
    override fun showProgress()
    {
        this.progressBar.show()
    }
    
    override fun hideProgress()
    {
        this.progressBar.hide()
    }
    
    override fun navigateBackHome()
    {
        this.fragmentManager?.popBackStack()
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        
        this.listener?.setTitle(AddNoteFragment.TITLE)
        this.listener?.configFloatingButton(FloatingButtonType.DONE, true){
            val textTitle = this.find<EditText>(AddNoteUI.ID_TEXT_TITLE)
            if (textTitle.text.isBlank())
            {
                this.toast("Title is empty!")
            }
            else
            {
                val textContent = this.find<EditText>(AddNoteUI.ID_TEXT_CONTENT)
                val note = Note(0, textTitle.text.toString(), textContent.text.toString(), "")
                this.presenter.saveNote(note)
            }
        }
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            AddNoteUI().createView(AnkoContext.create(this.ctx, this, false))
    
    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        this.listener = if (context is AddNoteFragment.OnFragmentInteractionListener) context else throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
    }
    
    override fun onStart()
    {
        super.onStart()
        val image = this.find<ImageView>(AddNoteUI.ID_IMAGE_CONTENT)
        image.setOnClickListener {
            if (EasyPermissions.hasPermissions(this.ctx, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                this.onPermissionsGranted(AddNoteFragment.REQUEST_CODE, arrayListOf())
            }
            else
            {
                EasyPermissions.requestPermissions(this, "Need permissions get to next step.", AddNoteFragment.REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddNoteFragment.REQUEST_RESULT && resultCode == Activity.RESULT_OK)
        {
            val result = Matisse.obtainResult(data)
            val image = this.find<ImageView>(AddNoteUI.ID_IMAGE_CONTENT)
            image.setImageURI(result.first()!!)
        }
    }
    
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>)
    {
        if (requestCode == AddNoteFragment.REQUEST_CODE){
            Matisse.from(this)
                    .choose(MimeType.of(MimeType.GIF, MimeType.JPEG, MimeType.PNG))
                    .countable(true)
                    .maxSelectable(1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.75f)
                    .imageEngine(MyGlideEngine())
                    .forResult(AddNoteFragment.REQUEST_RESULT)
        }
    }
    
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>)
    {
        toast("Permissions are denied.")
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    
    override fun onDetach()
    {
        super.onDetach()
        listener = null
    }
    
    override fun onDestroy()
    {
        super.onDestroy()
        this.presenter.dispose()
    }
}


class AddNoteUI: AnkoComponent<Fragment>
{
    companion object
    {
        const val ID_TEXT_TITLE = 0x21
        const val ID_TEXT_CONTENT = 0x22
        const val ID_IMAGE_CONTENT = 0x23
    }
    
    override fun createView(ui: AnkoContext<Fragment>) = with(ui){
        constraintLayout {
            fitsSystemWindows = true
            
            editText {
                id = ID_TEXT_TITLE
                hint = "Title"
                textSize = 18.0f
                textColor = Color.MAGENTA
                backgroundResource = R.drawable.text_field_border
            }.lparams(width = matchConstraint, height = wrapContent){
                padding = dip(8)
                margin = dip(8)
            }
            
            editText {
                id = ID_TEXT_CONTENT
                hint = "Content"
                lines = 10
                backgroundResource = R.drawable.text_field_border
                gravity = Gravity.TOP or Gravity.START
            }.lparams(width = matchConstraint, height = wrapContent)
    
            imageView {
                id = ID_IMAGE_CONTENT
                scaleType = ImageView.ScaleType.CENTER
                imageResource = R.mipmap.ic_launcher
            }.lparams(width = wrapContent, height = matchConstraint)
        
            applyConstraintSet {
                connect(
                        TOP of ID_TEXT_TITLE to TOP of PARENT_ID margin dip(8),
                        START of ID_TEXT_TITLE to START of PARENT_ID margin dip(8),
                        END of ID_TEXT_TITLE to END of PARENT_ID margin dip(8),
        
                        TOP of ID_TEXT_CONTENT to BOTTOM of ID_TEXT_TITLE margin dip(8),
                        START of ID_TEXT_CONTENT to START of PARENT_ID margin dip(8),
                        END of ID_TEXT_CONTENT to END of PARENT_ID margin dip(8),
                        //BOTTOM of ID_TEXT_CONTENT to TOP of ID_IMAGE_CONTENT margin dip(8),
        
                        START of ID_IMAGE_CONTENT to START of PARENT_ID margin dip(8),
                        END of ID_IMAGE_CONTENT to END of PARENT_ID margin dip(8),
                        TOP of ID_IMAGE_CONTENT to BOTTOM of ID_TEXT_CONTENT margin dip(8)
                       )
            }
        
        }
    }
}
