package me.liuqingwen.android.projectbasicmvp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import me.liuqingwen.android.projectbasicmvp.model.Movie
import me.liuqingwen.android.projectbasicmvp.presenter.MovieListPresenter
import me.liuqingwen.android.projectbasicmvp.ui.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast

/**
 * Created by Qingwen on 2018-2-17, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-17
 * @Package: me.liuqingwen.android.projectbasicmvp.model in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

@SuppressLint("CheckResult")
class MovieListFragment : BasicListFragment(), IMovieView
{
    companion object
    {
        private const val DEBUG = true
        
        fun newInstance()  = MovieListFragment()
    }
    
    private var moviePreloadCacheCount: Int = 4
        get() = this.movieListColumnCount * 4
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    
    private val presenter by lazy(LazyThreadSafetyMode.NONE) { MovieListPresenter(this) }
    
    override val title: String = "Top250"
    
    override fun scrollToTop()
    {
        this.smoothScrollTo(0)
    }
    
    override fun onLoadSuccess(it: List<Movie>)
    {
        val lastIndex = this.dataList.size
        this.dataList += it
        val columnCount = this.dataList.size / 50 + 1
        if(this.setListColumnCount(columnCount)) Unit else this.adapter.notifyDataSetChanged()
        if (lastIndex > 0)
        {
            this.smoothScrollTo(lastIndex)
        }
        this.onLoadFinish()
    }
    
    override fun onLoadError(message: String?)
    {
        this.alert("Info: ${message ?: "unknown error!"}", "Error") { negativeButton("Cancel"){} }.show()
        this.onLoadFinish()
    }
    
    override fun onLoadStarted()
    {
        this.swipeRefreshLayout.isRefreshing = true
        this.hideKeyboard()
    }
    
    private fun onLoadFinish()
    {
        this.swipeRefreshLayout.isRefreshing = false
        this.toast("Loading Finished!")
    }
    
    private fun smoothScrollTo(position: Int = 0)
    {
        val currentPosition = this.layoutManager.findFirstVisibleItemPosition()
        val threshold = this.movieListColumnCount * 4
        this.recyclerView.fastSmoothScrollToPosition(currentPosition, position, threshold)
    }
    
    private fun setListColumnCount(columnCount: Int): Boolean
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
    
    override fun bindView(movie: Movie, view: View)
    {
        val imageMovie = view.find<ImageView>(ID_DIALOG_IMAGE_MOVIE)
        val labelTitle = view.find<TextView>(ID_DIALOG_LABEL_TITLE)
        val labelInfo = view.find<TextView>(ID_DIALOG_LABEL_INFO)
        
        this.loadImageToView(movie.images.largeImage, imageMovie)
        labelTitle.text = movie.originalTitle
        val info = """Title: ${movie.TranslationTitle} (${movie.year})
            |By: ${movie.movieDirectors.joinToString { it.name }}
            |Stars: ${movie.movieStars.joinToString { it.name }}
            |Genres: ${movie.genres.joinToString()}
            |Link: ${movie.webUrl}
        """.trimMargin()
        labelInfo.text = info
    }
    
    private fun loadImageToView(url: String, imageView: ImageView)
    {
        this.glideRequest.load(url).into(imageView)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = MovieListViewUI().createView(UI{})
        this.swipeRefreshLayout = view.find(ID_LAYOUT_SWIPE_REFRESH)
        
        this.recyclerView = view.find(ID_LIST_MOVIE)
        val preloader = RecyclerViewPreloader<Movie>(this.glideManager, this.adapter, ViewPreloadSizeProvider<Movie>(), this.moviePreloadCacheCount)
        this.recyclerView.setItemViewCacheSize(0)
        this.recyclerView.addOnScrollListener(preloader)
        this.recyclerView.layoutManager = this.layoutManager
        this.recyclerView.adapter = this.adapter
        
        return view
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.presenter.onCreate()
    }
    
    override fun onStart()
    {
        super.onStart()
        
        this.swipeRefreshLayout.setOnRefreshListener {
            this.presenter.loadMovieList()
        }
        
        this.presenter.onStart()
    }
    
    override fun onDestroy()
    {
        super.onDestroy()
        
        this.presenter.onDestroy()
    }
}