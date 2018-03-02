package me.liuqingwen.android.projectbasicmvp.view

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import me.liuqingwen.android.projectbasicmvp.model.Movie
import me.liuqingwen.android.projectbasicmvp.presenter.MovieSearchPresenter
import me.liuqingwen.android.projectbasicmvp.ui.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast

/**
 * Created by Qingwen on 2018-3-1, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-1
 * @Package: me.liuqingwen.android.projectbasicmvp.model in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class MovieSearchFragment : BasicListFragment(), IMovieView
{
    companion object
    {
        fun newInstance()  = MovieSearchFragment()
    }
    
    private var moviePreloadCacheCount: Int = 4
        get() = this.movieListColumnCount * 4
    private lateinit var recyclerView: RecyclerView
    private lateinit var textSearch:EditText
    private lateinit var buttonSearch:Button
    
    private val presenter by lazy(LazyThreadSafetyMode.NONE) { MovieSearchPresenter(this) }
    
    override val title: String = "Search Movies"
    
    override fun scrollToTop()
    {
        this.smoothScrollTo(0)
    }
    
    override fun onAppbarScrollChange(layoutHeight: Int, offset: Int)
    {
        this.textSearch.translationY = layoutHeight + offset.toFloat()
        this.buttonSearch.translationY = layoutHeight + offset.toFloat()
    }
    
    override fun onLoadSuccess(it: List<Movie>)
    {
        this.dataList.clear()
        this.dataList += it
        val columnCount = this.dataList.size / 50 + 1
        if(this.setListColumnCount(columnCount)) Unit else this.adapter.notifyDataSetChanged()
        this.scrollToTop()
        this.onLoadFinish()
    }
    
    override fun onLoadError(message: String?)
    {
        this.alert("Info: ${message ?: "unknown error!"}", "Error") { negativeButton("Cancel"){} }.show()
        this.onLoadFinish()
    }
    
    override fun onLoadStarted() = this.hideKeyboard()
    
    private fun onLoadFinish()
    {
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
        val view = MovieSearchViewUI().createView(UI{})
        
        this.textSearch = view.find(ID_TEXT_SEARCH)
        this.buttonSearch = view.find(ID_BUTTON_SEARCH)
        this.buttonSearch.setOnClickListener {
            val text = this.textSearch.text.toString()
            this.presenter.searchMovieList(text)
        }
        
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
        
        this.presenter.onStart()
    }
    
    override fun onDestroy()
    {
        super.onDestroy()
        
        this.presenter.onDestroy()
    }
}