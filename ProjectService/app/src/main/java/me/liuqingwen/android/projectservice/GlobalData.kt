package me.liuqingwen.android.projectservice

import android.os.Looper

/**
 * Created by Qingwen on 2018-5-14, project: ProjectService.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-14
 * @Package: me.liuqingwen.android.projectservice in project: ProjectService
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

object GlobalCache
{
    private val data = mutableListOf<Movie>()
    
    fun addMovies(movies: List<Movie>)
    {
        this.data.addAll(movies)
    }
    
    fun clearData()
    {
        this.data.clear()
    }
    
    fun searchMovie(text : String):List<Movie>
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            throw RuntimeException("You cannot run this method on Android UI Thread!")
        }
        
        if (text.isBlank())
        {
            return data
        }
        
        fun containsText(vararg strings:String) : Boolean
        {
            return strings.any { it.contains(text, false) }
        }
    
        return data.filter { containsText(*it.strings.toTypedArray()) }
    }
}

val Movie.strings
    get() = listOf(this.TranslationTitle,
                   this.originalTitle,
                   this.genres.joinToString(separator = ""),
                   this.movieDirectors.joinToString(separator = "") { it.name },
                   this.movieStars.joinToString(separator = "") { it.name }
                  )