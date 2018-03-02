package me.liuqingwen.android.projectbasicmvp.presenter

import io.reactivex.disposables.CompositeDisposable
import me.liuqingwen.android.projectbasicmvp.model.APIMovieService
import me.liuqingwen.android.projectbasicmvp.view.IMovieView

/**
 * Created by Qingwen on 2018-3-1, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-1
 * @Package: me.liuqingwen.android.projectbasicmvp.presenter in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class MovieSearchPresenter(private val view: IMovieView)
{
    private var startIndex = 0
    private var countLoaded = 20
    private var isRefreshing = false
    
    private val disposables by lazy(LazyThreadSafetyMode.NONE) { CompositeDisposable() }
    
    fun onCreate() = Unit
    
    fun onStart() = Unit
    
    fun onDestroy()
    {
        this.disposables.dispose()
    }
    
    fun searchMovieList(text: String)
    {
        if (this.isRefreshing || text.isEmpty())
        {
            return
        }
        
        this.view.onLoadStarted()
        
        this.disposables.add(
                APIMovieService.searchMovie(text, onSuccess = {
                    this.startIndex += it.size
                    if (it.isNotEmpty())
                    {
                        this.view.onLoadSuccess(it)
                    }
                    else
                    {
                        this.view.onLoadError("Empty list!")
                    }
                }, onFailure = {
                    this.view.onLoadError(it.message)
                }))
    }
}