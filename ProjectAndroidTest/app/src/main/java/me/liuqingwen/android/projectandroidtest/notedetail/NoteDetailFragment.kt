package me.liuqingwen.android.projectandroidtest.notedetail

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import kotlinx.android.synthetic.main.laoyut_fragment_add_note.view.*
import kotlinx.android.synthetic.main.layout_fragment_note_detail.view.*
import me.liuqingwen.android.projectandroidtest.FloatingButtonType
import me.liuqingwen.android.projectandroidtest.IMainInteractionListener

import me.liuqingwen.android.projectandroidtest.R
import me.liuqingwen.android.projectandroidtest.data.InMemoryNotesRepository
import me.liuqingwen.android.projectandroidtest.data.InMemoryNotesServiceApi
import me.liuqingwen.android.projectandroidtest.data.Note
import me.liuqingwen.android.projectandroidtest.data.NotesRepositories
import me.liuqingwen.android.projectandroidtest.noteslist.NotesListFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.find

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NoteDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NoteDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class NoteDetailFragment : Fragment(), INoteDetailContract.IView
{
    interface OnFragmentInteractionListener:IMainInteractionListener
    
    companion object
    {
        private const val PARAM_NOTE_ID = "NOTE_ID"
        
        fun newInstance(noteId:Int) = NoteDetailFragment().apply {
            this.arguments = Bundle().apply { this.putInt(NoteDetailFragment.PARAM_NOTE_ID, noteId) }
        }
    }
    
    private var listener: OnFragmentInteractionListener? = null
    private val repository by lazy(LazyThreadSafetyMode.NONE) { NotesRepositories.getRepository(InMemoryNotesServiceApi()) }
    private val presenter by lazy(LazyThreadSafetyMode.NONE) { NoteDetailPresenter(this.repository, this) }
    private val request by lazy(LazyThreadSafetyMode.NONE) { Glide.with(this).applyDefaultRequestOptions(RequestOptions().apply {
        this.error(R.mipmap.ic_launcher)
        this.placeholder(R.mipmap.ic_launcher)
        this.centerCrop()
    }).asDrawable() }
    private var noteId : Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        
        this.arguments?.let {
            this.noteId = it.getInt(NoteDetailFragment.PARAM_NOTE_ID)
        }
        
        this.listener?.setTitle("Notes: ${this.noteId}")
        this.listener?.configFloatingButton(FloatingButtonType.NONE, false, null)
    }
    
    override fun displayMissingNote()
    {
        val labelTitle = this.find<TextView>(NoteDetailUI.ID_LABEL_TITLE)
        labelTitle.text = ""
        val labelContent = this.find<TextView>(NoteDetailUI.ID_LABEL_CONTENT)
        labelContent.text = "No data!"
    
        val imageContent = this.find<ImageView>(NoteDetailUI.ID_IMAGE_CONTENT)
        imageContent.setImageDrawable(this.resources.getDrawable(R.mipmap.ic_launcher_round))
    }
    
    override fun showProgress()
    {
        val progressBar = this.find<ProgressBar>(NoteDetailUI.ID_PROGRESS_BAR)
        progressBar.visibility = View.VISIBLE
    }
    
    override fun hideProgress()
    {
        val progressBar = this.find<ProgressBar>(NoteDetailUI.ID_PROGRESS_BAR)
        progressBar.visibility = View.GONE
    }
    
    override fun displayNote(note: Note)
    {
        val labelTitle = this.find<TextView>(NoteDetailUI.ID_LABEL_TITLE)
        labelTitle.text = note.title
        val labelContent = this.find<TextView>(NoteDetailUI.ID_LABEL_CONTENT)
        labelContent.text = note.content
        
        val imageContent = this.find<ImageView>(NoteDetailUI.ID_IMAGE_CONTENT)
        this.request.load(note.image).into(imageContent)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            NoteDetailUI().createView(AnkoContext.create(this.ctx, this, false))
    
    override fun onStart()
    {
        super.onStart()
        
        this.presenter.getNote(this.noteId)
    }
    
    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        
        this.listener = if (context is OnFragmentInteractionListener) context else throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
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


class NoteDetailUI:AnkoComponent<Fragment>
{
    companion object
    {
        const val ID_PROGRESS_BAR = 0x11
        const val ID_LABEL_TITLE = 0x12
        const val ID_LABEL_CONTENT = 0x13
        const val ID_IMAGE_CONTENT = 0x14
    }
    
    override fun createView(ui: AnkoContext<Fragment>) = with(ui){
        scrollView {
            
            constraintLayout {
                
                horizontalProgressBar {
                    id = ID_PROGRESS_BAR
                    isIndeterminate = true
                }.lparams(width = matchConstraint, height = wrapContent)
                
                textView {
                    id = ID_LABEL_TITLE
                    textSize = 24.0f
                    textColor = Color.MAGENTA
                }.lparams(width = matchConstraint, height = wrapContent)
                
                textView {
                    id = ID_LABEL_CONTENT
                    textSize = 18.0f
                }.lparams(width = matchConstraint, height = wrapContent)
                
                imageView {
                    id = ID_IMAGE_CONTENT
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }.lparams(width = wrapContent, height = wrapContent)
                
                applyConstraintSet {
                    connect(
                            TOP of ID_PROGRESS_BAR to TOP of PARENT_ID margin dip(8),
                            START of ID_PROGRESS_BAR to START of PARENT_ID margin dip(8),
                            END of ID_PROGRESS_BAR to END of PARENT_ID margin dip(8),
                            
                            TOP of ID_LABEL_TITLE to BOTTOM of ID_PROGRESS_BAR margin dip(8),
                            START of ID_LABEL_TITLE to START of PARENT_ID margin dip(8),
                            END of ID_LABEL_TITLE to END of PARENT_ID margin dip(8),

                            TOP of ID_LABEL_CONTENT to BOTTOM of ID_LABEL_TITLE margin dip(8),
                            START of ID_LABEL_CONTENT to START of PARENT_ID margin dip(8),
                            END of ID_LABEL_CONTENT to END of PARENT_ID margin dip(8),
        
                            START of ID_IMAGE_CONTENT to START of PARENT_ID margin dip(8),
                            END of ID_IMAGE_CONTENT to END of PARENT_ID margin dip(8),
                            TOP of ID_IMAGE_CONTENT to BOTTOM of ID_LABEL_CONTENT margin dip(8)
                           )
                }
                
            }.lparams(width = matchParent, height = matchParent)
        }
    }
}
