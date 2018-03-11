package me.liuqingwen.android.projectmaterialanimation

import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import org.jetbrains.anko.frameLayout

//Animation from: Build awesome animations with 7 lines of code using ConstraintLayout
//https://android.jlelse.eu/build-awesome-animations-with-7-lines-of-code-using-constraintlayout-854e8fd3ad93

const val SHARED_TRANSITION_NAME = "shared_image_transition_name"
private const val ID_LAYOUT_CONTAINER = 0x01
class MainActivity : AppCompatActivity(), ListFragment.IFragmentInteractionListener
{
    private lateinit var listFragment: ListFragment
    
    override fun onMovieItemSelect(itemImage: View, movie: Movie)
    {
        this.supportActionBar?.title = movie.originalTitle
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        ViewCompat.setTransitionName(itemImage, SHARED_TRANSITION_NAME)
        val detailFragment = DetailFragment.newInstance(movie)
        this.supportFragmentManager.beginTransaction()
                .addSharedElement(itemImage, SHARED_TRANSITION_NAME)
                //.hide(this.listFragment)
                .replace(ID_LAYOUT_CONTAINER, detailFragment, DetailFragment::class.java.simpleName)
                .addToBackStack(null)
                .commit()
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        frameLayout {
            fitsSystemWindows = true
            id = ID_LAYOUT_CONTAINER
        }
        
        this.init()
    }
    
    private fun init()
    {
        this.supportActionBar?.title = "Movie List"
        this.listFragment = ListFragment.newInstance()
        this.supportFragmentManager.beginTransaction()
                .add(ID_LAYOUT_CONTAINER, this.listFragment, ListFragment::class.java.simpleName)
                //.addToBackStack(null) //if added then onBackPress will remove it to be a blank in Activity
                .commit()
    }
    
    override fun onBackPressed()
    {
        super.onBackPressed()
        this.supportActionBar?.title = "Movie List"
        this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        this.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}