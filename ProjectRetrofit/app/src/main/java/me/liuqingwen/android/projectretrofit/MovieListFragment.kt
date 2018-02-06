package me.liuqingwen.android.projectretrofit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.ImageViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.ViewPreloadSizeProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import kotlin.math.absoluteValue

@SuppressLint("CheckResult")
class MovieListFragment : Fragment()
{
    interface OnListFragmentInteractionListener
    {
        val onItemClickListener: (Movie) -> Unit
        fun hideToolBar(hidden: Boolean)
    }
    
    companion object
    {
        private const val DEBUG = false
        private const val ARG_COLUMN_COUNT = "arg_column_count"
        
        fun newInstance(columnCount: Int)  = MovieListFragment().apply { this.arguments = bundle(MovieListFragment.ARG_COLUMN_COUNT to columnCount) }
    }
    
    private var isRefreshing = false
    private var start = 0
    private var count = 20
    private var movieListColumnCount = 1
    private var moviePreloadCacheCount: Int = 4
        get() = this.movieListColumnCount * 4
    private var activityListener: OnListFragmentInteractionListener? = null
    private val glideManager by lazy(LazyThreadSafetyMode.NONE) { Glide.with(this) }
    private val glideRequest by lazy(LazyThreadSafetyMode.NONE) {
        this.glideManager.applyDefaultRequestOptions( RequestOptions().apply {
            if (DEBUG)
            {
                this.diskCacheStrategy(DiskCacheStrategy.NONE)
                this.skipMemoryCache(true)
            }
            this.placeholder(R.drawable.placeholder)
            this.error(R.drawable.image_load_error)
            this.fitCenter()
        } ).asDrawable()
    }
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<Movie>() }
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { MovieListAdapter(this.dataList, this.movieListColumnCount, this.glideRequest, this.activityListener?.onItemClickListener)}
    private val layoutManager by lazy(LazyThreadSafetyMode.NONE) { GridLayoutManager(this.context, this.movieListColumnCount) }
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    
    private fun loadMovieList() {
        if (this.isRefreshing)
        {
            return
        }
    
        fun loadOver()
        {
            this.activityListener?.hideToolBar(false)
            this.swipeRefreshLayout.isRefreshing = false
            this.isRefreshing = false
            
            this.toast("Loading Finished!")
        }
        
        this.activityListener?.hideToolBar(true)
        APIService.getMovieListObservable(this.start, this.count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.subjects }
                .subscribe({
                               this.start += it.size
                               //this.dataList.clear()
                               this.dataList += it
                               if (this.setListColumnCount(this.start / 50 + 1)) Unit else this.adapter.notifyDataSetChanged()
                               this.smoothScrollTo(this.start - it.size)
                           },
                           {
                               this.alert("Info: ${it.message}", "Error") { negativeButton("Cancel"){} }.show()
                               loadOver()
                           },
                           {
                               loadOver()
                           })
    }
    
    fun smoothScrollTo(position: Int = 0)
    {
        val currentPosition = this.layoutManager.findFirstVisibleItemPosition()
        val threshold = this.movieListColumnCount * 4
        if ((currentPosition - position).absoluteValue > threshold)
        {
            this.recyclerView.scrollToPosition(if (currentPosition > position) threshold + position else position - threshold)
        }
        this.recyclerView.smoothScrollToPosition(position)
    }
    
    fun setListColumnCount(columnCount: Int): Boolean
    {
        if (this.movieListColumnCount == columnCount || columnCount < 1)
        {
            return false
        }
        
        this.movieListColumnCount = columnCount
        this.layoutManager.spanCount = columnCount
    
        this.adapter.listColumnCount = columnCount
        this.recyclerView.adapter = null
        this.recyclerView.layoutManager = null
        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = this.layoutManager
        
        return true
    }
    
    fun loadImageToView(url: String, imageView: ImageView)
    {
        this.glideRequest.load(url).into(imageView)
    }
    
    private fun bundle(data: Pair<String, Int>) = Bundle().apply { this.putInt(data.first, data.second) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.movieListColumnCount = if (this.arguments != null) this.arguments!!.getInt(MovieListFragment.ARG_COLUMN_COUNT) else this.movieListColumnCount
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = MovieListViewUI().createView(UI{})
        this.swipeRefreshLayout = view.find<SwipeRefreshLayout>(ID_SWIPE_REFRESH)
        
        this.recyclerView = view.find<RecyclerView>(ID_RECYCLER_VIEW)
        val preloader = RecyclerViewPreloader<Movie>(this.glideManager, this.adapter, ViewPreloadSizeProvider<Movie>(), this.moviePreloadCacheCount)
        this.recyclerView.setItemViewCacheSize(0)
        this.recyclerView.addOnScrollListener(preloader)
        this.recyclerView.layoutManager = this.layoutManager
        this.recyclerView.adapter = this.adapter
        
        return view
    }
    
    override fun onStart()
    {
        super.onStart()
        
        this.swipeRefreshLayout.setOnRefreshListener {
            this.loadMovieList()
        }
        this.swipeRefreshLayout.isRefreshing = true
        this.loadMovieList()
    }

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        this.activityListener = if (context is OnListFragmentInteractionListener) context else throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
    }

    override fun onDetach()
    {
        this.activityListener = null
        super.onDetach()
    }
}
