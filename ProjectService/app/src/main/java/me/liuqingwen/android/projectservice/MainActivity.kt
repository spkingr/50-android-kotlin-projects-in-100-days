package me.liuqingwen.android.projectservice

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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

class MainActivity : AppCompatActivity()
{
    
    private var disposable:Disposable? = null
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { ArrayList<Movie>() }
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { CustomAdapter(this.dataList, this.glideRequest) }
    private val glideRequest by lazy(LazyThreadSafetyMode.NONE) { Glide.with(this).applyDefaultRequestOptions(RequestOptions().apply{
        this.error(R.drawable.image_load_error)
        this.placeholder(R.drawable.placeholder)
        this.centerCrop()
    }).asDrawable() }
    
    private lateinit var recyclerView : RecyclerView
    private lateinit var buttonSearch : Button
    private lateinit var textSearch : EditText
    private lateinit var progressBar : ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        MainUI().setContentView(this)
        
        this.init()
    }
    
    private fun init()
    {
        this.recyclerView = this.find(MainUI.ID_RECYCLER_VIEW)
        this.buttonSearch = this.find(MainUI.ID_BUTTON_SEARCH)
        this.textSearch = this.find(MainUI.ID_TEXT_SEARCH)
        this.progressBar = this.find(MainUI.ID_PROGRESS_BAR)
        
        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        
        this.hideProgress()
    }
    
    override fun onStart()
    {
        super.onStart()
    
        val intent = Intent(this, MyService::class.java)
        this.bindService(intent, object : ServiceConnection{
            override fun onServiceDisconnected(name: ComponentName?)
            {
                this@MainActivity.toast("Service connected")
            }
            override fun onServiceConnected(name: ComponentName?, service: IBinder?)
            {
                val binder = service as? MyService.MyBinder
                binder?.startFetchData()
            }
        }, Service.BIND_AUTO_CREATE)
        
        this.createButtonObservable().mergeWith(this.createTextWatchObservable())
                .subscribeOn(AndroidSchedulers.mainThread()) //no need.
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext{ this.showProgress() }
                .observeOn(Schedulers.io())
                .map { GlobalCache.searchMovie(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    this.hideProgress()
                    this.displayMovies(it)
                }
    }
    
    private fun createButtonObservable() = Observable.create<String> {emitter ->
        this.buttonSearch.setOnClickListener {
            this.hideKeyboard()
            emitter.onNext(this.textSearch.text.toString())
        }
        
        emitter.setCancellable {
            this.buttonSearch.setOnClickListener(null)
        }
    }
    
    private fun createTextWatchObservable() = Observable.create<String> {emitter ->
        val textWatcher = object : TextWatcher{
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                s?.let{ emitter.onNext(it.toString()) }
            }
        }
    
        this.textSearch.addTextChangedListener(textWatcher)
        
        emitter.setCancellable {
            this.textSearch.removeTextChangedListener(textWatcher)
        }
        
    }.filter { it.isNotBlank() }.debounce(2L, TimeUnit.SECONDS)
    
    private fun hideKeyboard()
    {
        this.currentFocus?.let {
            this.inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
    
    private fun displayMovies(movies: List<Movie>)
    {
        this.dataList.clear()
        if (movies.isNotEmpty())
        {
            this.dataList.addAll(movies)
        }
        else
        {
            this.toast("No movies found")
        }
        this.adapter.notifyDataSetChanged()
    }
    
    private fun showProgress()
    {
        this.progressBar.visibility = View.VISIBLE
    }
    
    private fun hideProgress()
    {
        this.progressBar.visibility = View.GONE
    }
    
    override fun onStop()
    {
        if (this.disposable?.isDisposed == false)
        {
            this.disposable?.dispose()
        }
        super.onStop()
    }
}

class MainUI:AnkoComponent<Activity>
{
    companion object
    {
        const val ID_TOOL_BAR = 0x01
        const val ID_TEXT_SEARCH = 0x02
        const val ID_BUTTON_SEARCH = 0x03
        const val ID_RECYCLER_VIEW = 0x04
        const val ID_PROGRESS_BAR = 0x05
    }
    
    override fun createView(ui: AnkoContext<Activity>) = with(ui){
        coordinatorLayout {
            appBarLayout {
                toolbar {
                    id = ID_TOOL_BAR
                    title = "Search Movies"
                    setTitleTextColor(Color.WHITE)
                }.lparams(width = matchParent, height = dip(56))
            }.lparams(width = matchParent, height = wrapContent)
            
            constraintLayout {
                editText {
                    id = ID_TEXT_SEARCH
                    hint = "Type to search"
                }.lparams(width = matchConstraint, height = wrapContent)
                
                button("Search") {
                    id = ID_BUTTON_SEARCH
                }.lparams(width = wrapContent, height = wrapContent)
                
                recyclerView {
                    id = ID_RECYCLER_VIEW
                }.lparams(width = matchConstraint, height = matchConstraint)
                
                progressBar {
                    id = ID_PROGRESS_BAR
                }.lparams(width = wrapContent, height = wrapContent)
                
                applyConstraintSet {
                    connect(
                            TOP of ID_BUTTON_SEARCH to TOP of PARENT_ID margin dip(16),
                            END of ID_BUTTON_SEARCH to END of PARENT_ID margin dip(8),

                            START of ID_TEXT_SEARCH to START of PARENT_ID margin dip(8),
                            END of ID_TEXT_SEARCH to START of ID_BUTTON_SEARCH margin dip(8),
                            BOTTOM of ID_TEXT_SEARCH to BOTTOM of ID_BUTTON_SEARCH,

                            START of ID_RECYCLER_VIEW to START of PARENT_ID margin dip(8),
                            END of ID_RECYCLER_VIEW to END of PARENT_ID margin dip(8),
                            TOP of ID_RECYCLER_VIEW to BOTTOM of ID_TEXT_SEARCH margin dip(8),
                            BOTTOM of ID_RECYCLER_VIEW to BOTTOM of PARENT_ID,

                            START of ID_PROGRESS_BAR to START of PARENT_ID,
                            END of ID_PROGRESS_BAR to END of PARENT_ID,
                            TOP of ID_PROGRESS_BAR to BOTTOM of ID_TEXT_SEARCH margin dip(4)
                           )
                }
            }.lparams(width = matchParent, height = matchParent){
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }
}
