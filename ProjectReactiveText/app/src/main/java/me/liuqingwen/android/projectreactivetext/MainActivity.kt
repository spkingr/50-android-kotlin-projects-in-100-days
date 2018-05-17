package me.liuqingwen.android.projectreactivetext

import android.os.Bundle
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import java.util.concurrent.TimeUnit

internal const val ID_TOOL_BAR = 0x01
internal const val ID_TEXT_SEARCH = 0x02
internal const val ID_BUTTON_SEARCH = 0x03
internal const val ID_RECYCLER_VIEW = 0x04
internal const val ID_PROGRESS_BAR = 0x05

class MainActivity : AppCompatActivity(), AnkoLogger
{
    private val searchEngine by lazy(LazyThreadSafetyMode.NONE) { SearchEngine (this) }
    private val adapter = ListAdapter()
    private var disposable:Disposable? = null
    
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonSearch: Button
    private lateinit var textSearch: EditText
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        coordinatorLayout {
            fitsSystemWindows = true
            
            appBarLayout {
                toolbar {
                    id = ID_TOOL_BAR
                    title = "Reactive Text Search"
                }.lparams(width = matchParent, height = wrapContent){
                    scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                }
            }.lparams(width = matchParent, height = wrapContent)
            
            constraintLayout {
                editText {
                    id = ID_TEXT_SEARCH
                    hint = "Type text to search"
                    this@MainActivity.textSearch = this
                }.lparams(width = matchConstraint, height = wrapContent)
                
                button("Search") {
                    id = ID_BUTTON_SEARCH
                    transformationMethod = null
                    this@MainActivity.buttonSearch = this
                }.lparams(width = wrapContent, height = wrapContent)
                
                recyclerView {
                    id = ID_RECYCLER_VIEW
                    
                    adapter = this@MainActivity.adapter
                    layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    this.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
                    visibility = View.GONE
                    this@MainActivity.recyclerView = this
                }.lparams(width = matchConstraint, height = matchConstraint)
                
                progressBar {
                    id = ID_PROGRESS_BAR
                    visibility = View.GONE
                    this@MainActivity.progressBar = this
                }.lparams(width = wrapContent, height = wrapContent)
                
                applyConstraintSet {
                    connect(
                            TOP of ID_BUTTON_SEARCH to TOP of PARENT_ID margin dip(16),
                            END of ID_BUTTON_SEARCH to END of PARENT_ID margin dip(8),
                            
                            START of ID_TEXT_SEARCH to START of PARENT_ID margin dip(8),
                            END of ID_TEXT_SEARCH to START of ID_BUTTON_SEARCH margin dip(8),
                            BOTTOM of ID_TEXT_SEARCH to BOTTOM of ID_BUTTON_SEARCH,

                            START of ID_RECYCLER_VIEW to START of PARENT_ID margin dip(16),
                            END of ID_RECYCLER_VIEW to END of PARENT_ID margin dip(16),
                            TOP of ID_RECYCLER_VIEW to BOTTOM of ID_TEXT_SEARCH margin dip(2),
                            BOTTOM of ID_RECYCLER_VIEW to BOTTOM of PARENT_ID,

                            START of ID_PROGRESS_BAR to START of PARENT_ID,
                            END of ID_PROGRESS_BAR to END of PARENT_ID,
                            TOP of ID_PROGRESS_BAR to BOTTOM of ID_TEXT_SEARCH margin dip(8)
                           )
                }
            }.lparams(width = matchParent, height = matchParent){
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
        
        this.init()
    }
    
    private fun init()
    {
    }
    
    private fun hideKeyboard()
    {
        this.currentFocus?.let {
            this.inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
    
    override fun onStart()
    {
        super.onStart()
        val observable = Observable.merge(this.createButtonObservable(), this.createTextWatchObservable())
        this.disposable = observable
                //.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { this.showProgress() }
                .observeOn(Schedulers.io())
                .map { this.searchEngine.search(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    this.hideProgress()
                    this.showSearchResult(it)
                }
    }
    
    private fun showProgress()
    {
        this.progressBar.visibility = View.VISIBLE
        this.recyclerView.visibility = View.GONE
    }
    
    private fun hideProgress()
    {
        this.progressBar.visibility = View.GONE
        this.recyclerView.visibility = View.VISIBLE
    }
    
    private fun showSearchResult(result : List<String>)
    {
        this.adapter.dataList = if(result.isEmpty()) listOf("No Result!") else result
    }
    
    private fun createButtonObservable() = Observable.create<String>{
        this.buttonSearch.setOnClickListener{ _ ->
            this.hideKeyboard()
            it.onNext(this.textSearch.text.toString())
        }
        it.setCancellable {
            this.buttonSearch.setOnClickListener(null)
        }
    }
    
    private fun createTextWatchObservable(minLength: Int = 2, delayTime: Long = 1000) = Observable.create<String> {
        val textWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                s?.toString()?.run { it.onNext(this) }
            }
        }
        this.textSearch.addTextChangedListener(textWatcher)
        it.setCancellable { this.textSearch.removeTextChangedListener(textWatcher) }
    }.filter{ it.length >= minLength }.debounce(delayTime, TimeUnit.MILLISECONDS)
    
    override fun onStop()
    {
        if (this.disposable?.isDisposed == false)
        {
            this.disposable?.isDisposed
        }
        super.onStop()
    }
}

class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
{
    fun bind(text: String)
    {
        (this.itemView as TextView).text = text
    }
}

class ListAdapter : RecyclerView.Adapter<ItemViewHolder>()
{
    var dataList = emptyList<String>()
        set(value)
        {
            field = value
            this.notifyDataSetChanged()
        }
    
    override fun getItemCount() = this.dataList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(TextView(parent.context).apply {
        this.textSize = 24.0f
        this.topPadding = 8
        this.bottomPadding = 8
    })
    
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(this.dataList[position])
}
