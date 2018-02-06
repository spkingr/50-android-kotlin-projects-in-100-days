package me.liuqingwen.android.projectretrofit

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), MovieListFragment.OnListFragmentInteractionListener
{
    private var column = 1
    private var title = "Top250"
    private val fragment by lazy(LazyThreadSafetyMode.NONE) { MovieListFragment.newInstance(this.column) }
    
    override val onItemClickListener: (Movie) -> Unit = {
        alert {
            customView = MovieDialogUI().createView(AnkoContext.create(this.ctx)).apply { this@MainActivity.bindView(it, this) }
            negativeButton("OK"){ }
        }.show()
    }
    
    override fun hideToolBar(hidden: Boolean)
    {
        if (hidden) this.supportActionBar?.hide() else this.supportActionBar?.show()
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        MainUI(this.title).setContentView(this)
        
        val toolbar = this.find<Toolbar>(ID_TOOL_BAR)
        this.setSupportActionBar(toolbar)
        val buttonToTop = this.find<FloatingActionButton>(ID_BUTTON_UP)
        buttonToTop.setOnClickListener { this.fragment.smoothScrollTo(0) }
        
        this.supportFragmentManager.beginTransaction().add(ID_FRAGMENT_CONTAINER, this.fragment, MainActivity::class.java.simpleName).commit()
    }
    
    private fun bindView(movie: Movie, view: View)
    {
        val imageMovie = view.find<ImageView>(ID_IMAGE_MOVIE)
        val labelTitle = view.find<TextView>(ID_LABEL_TITLE)
        val labelInfo = view.find<TextView>(ID_LABEL_INFO)
        
        this.fragment.loadImageToView(movie.images.largeImage, imageMovie)
        labelTitle.text = movie.originalTitle
        val info = """Title: ${movie.TranslationTitle} (${movie.year})
            |By: ${movie.movieDirectors.map { it.name }.joinToString()}
            |Stars: ${movie.movieStars.map { it.name }.joinToString()}
            |Genres: ${movie.genres.joinToString()}
            |Link: ${movie.webUrl}
        """.trimMargin()
        labelInfo.text = info
    }
}