package me.liuqingwen.android.projectbasicmvp.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import me.liuqingwen.android.projectbasicmvp.R
import me.liuqingwen.android.projectbasicmvp.model.Movie
import me.liuqingwen.android.projectbasicmvp.ui.MovieDialogUI
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.support.v4.alert

/**
 * Created by Qingwen on 2018-3-1, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-1
 * @Package: me.liuqingwen.android.projectbasicmvp.view in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

@SuppressLint("CheckResult")
abstract class BasicListFragment:Fragment()
{
    companion object
    {
        private const val DEBUG = true
    }
    
    abstract val title: String
    
    protected val glideManager by lazy(LazyThreadSafetyMode.NONE) { Glide.with(this) }
    protected val glideRequest by lazy(LazyThreadSafetyMode.NONE) {
        this.glideManager.applyDefaultRequestOptions( RequestOptions().apply {
            if (BasicListFragment.DEBUG)
            {
                this.diskCacheStrategy(DiskCacheStrategy.NONE)
                this.skipMemoryCache(true)
            }
            this.placeholder(R.drawable.placeholder)
            this.error(R.drawable.image_load_error)
            this.fitCenter()
        } ).asDrawable()
    }
    
    protected var movieListColumnCount = 1
    protected val dataList by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<Movie>() }
    protected val adapter by lazy(LazyThreadSafetyMode.NONE) {
        MovieListAdapter(this.dataList, this.movieListColumnCount, this.glideRequest) {
            alert {
                customView = MovieDialogUI().createView(AnkoContext.create(this.ctx)).apply {
                    this@BasicListFragment.bindView(it, this)
                }
                negativeButton("OK") { }
            }.show()
        }
    }
    protected val layoutManager by lazy(LazyThreadSafetyMode.NONE) { GridLayoutManager(this.context, this.movieListColumnCount) }
    
    abstract fun scrollToTop()
    
    abstract fun bindView(movie: Movie, view: View)
    
    open fun onAppbarScrollChange(layoutHeight: Int, offset: Int) = Unit
}