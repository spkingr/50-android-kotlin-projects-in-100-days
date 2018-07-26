package me.liuqingwen.android.projectandroidtest.noteslist

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.laoyut_fragment_notes_list.*
import me.liuqingwen.android.projectandroidtest.FloatingButtonType
import me.liuqingwen.android.projectandroidtest.IMainInteractionListener
import me.liuqingwen.android.projectandroidtest.R
import me.liuqingwen.android.projectandroidtest.data.InMemoryNotesServiceApi
import me.liuqingwen.android.projectandroidtest.data.Note
import me.liuqingwen.android.projectandroidtest.data.NotesRepositories
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.support.v4.ctx

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NotesListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NotesListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class NotesListFragment : Fragment(), INotesListContract.IView, AnkoLogger
{
    interface OnFragmentInteractionListener:IMainInteractionListener
    {
        fun navigateToAddNote()
        fun navigateToNoteDetail(noteId:Int)
    }
    
    companion object
    {
        const val TITLE = "All Notes"
        fun newInstance() = NotesListFragment()
        
        private const val COLUMNS = 2
    }
    
    private var listener: OnFragmentInteractionListener? = null
    
    private val repository by lazy(LazyThreadSafetyMode.NONE) { NotesRepositories.getRepository(InMemoryNotesServiceApi()) }
    private val presenter by lazy(LazyThreadSafetyMode.NONE) { NotesListPresenter(this.repository, this) }
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { arrayListOf<Note>() }
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { NoteAdapter(this.dataList, NotesListFragment.COLUMNS){
        this.presenter.showNoteDetail(it)
    } }
    private val layoutManager by lazy(LazyThreadSafetyMode.NONE) { GridLayoutManager(this.ctx, NotesListFragment.COLUMNS, GridLayoutManager.VERTICAL, false) }
    
    override fun displayNotes(notes: List<Note>)
    {
        this.dataList.clear()
        this.dataList.addAll(notes)
        this.adapter.notifyDataSetChanged()
    }
    
    override fun showProgress()
    {
        this.layoutSwipeRefresh.isRefreshing = true
    }
    
    override fun hideProgress()
    {
        this.layoutSwipeRefresh.isRefreshing = false
    }
    
    override fun navigateToAddNote()
    {
        this.listener?.navigateToAddNote()
    }
    
    override fun navigateToNoteDetail(noteId:Int)
    {
        this.listener?.navigateToNoteDetail(noteId)
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.listener?.setTitle(NotesListFragment.TITLE)
    
        this.fragmentManager?.addOnBackStackChangedListener {
            if (this.fragmentManager?.backStackEntryCount == 0)
            {
                this.presenter.loadData()
            }
        }
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.laoyut_fragment_notes_list, container, false)
    
    override fun onStart()
    {
        super.onStart()
        this.recyclerNotesList.adapter = this.adapter
        this.recyclerNotesList.layoutManager = this.layoutManager
    
        this.layoutSwipeRefresh.setOnRefreshListener {
            this.presenter.loadData()
        }
    
        this.presenter.loadData()
    }
    
    override fun onResume()
    {
        super.onResume()
        this.listener?.setTitle(NotesListFragment.TITLE)
        this.listener?.configFloatingButton(FloatingButtonType.ADD, true){
            this.listener?.navigateToAddNote()
        }
    }
    
    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        this.listener = if (context is OnFragmentInteractionListener) context else throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
    }
    
    override fun onDetach()
    {
        super.onDetach()
        this.listener = null
    }
    
    override fun onDestroy()
    {
        super.onDestroy()
        this.presenter.dispose()
    }
}

class NoteViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
{
    fun bind(note:Note, onNoteClickListener:((Int)->Unit)? = null)
    {
        val labelTitle = this.itemView.find<TextView>(NoteItemUI.ID_LABEL_TITLE)
        labelTitle.text = note.title
        
        val labelContent = this.itemView.find<TextView>(NoteItemUI.ID_LABEL_CONTENT)
        labelContent.text = note.content
        
        onNoteClickListener?.let {callback->
            this.itemView.setOnClickListener { callback(note.id) }
        }
    }
}

class NoteAdapter(private val data:List<Note>, private val columns: Int, private val noteClickCallback:((Int)->Unit)? = null):RecyclerView.Adapter<NoteViewHolder>()
{
    override fun getItemCount() = this.data.size
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteViewHolder(NoteItemUI(this.columns).createView(AnkoContext.create(parent.context, false)))
    
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) = holder.bind(this.data[position], this.noteClickCallback)
}

class NoteItemUI(private val columns:Int):AnkoComponent<Context>
{
    companion object
    {
        const val ID_LABEL_TITLE = 0x01
        const val ID_LABEL_CONTENT =  0x02
    }
    
    override fun createView(ui: AnkoContext<Context>) = with(ui){
        val outMargin = 16
        val columnCount = this@NoteItemUI.columns
        val outPoint = Point().also { this.ctx.windowManager.defaultDisplay.getSize(it) }
        val outWidth = (outPoint.x - outMargin * columnCount * 2) / columnCount
        
        cardView {
            radius = 20.0f
            layoutParams = ViewGroup.MarginLayoutParams(outWidth, outWidth).also { it.margin = outMargin }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                elevation = 4.0f
            }
    
            verticalLayout {
                
                textView {
                    id = ID_LABEL_TITLE
                    textSize = 18.0f
                    textColor = Color.MAGENTA
                }.lparams(width = matchParent, height = wrapContent){
                    padding = dip(10)
                }
    
                textView {
                    id = ID_LABEL_CONTENT
                    textSize = 14.0f
                    textColor = Color.GRAY
                }.lparams(width = matchParent, height = matchParent){
                    padding = dip(10)
                }
            }.lparams(width = matchParent, height = matchParent)
        }
    }
}

