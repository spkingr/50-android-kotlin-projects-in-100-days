package me.liuqingwen.android.projectmaterialanimation

import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.constraint.ConstraintSet.PARENT_ID
import android.support.transition.*
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.graphics.drawable.toDrawable
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.find

/**
 * Created by Qingwen on 2018-3-10, project: ProjectMaterialAnimation.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-10
 * @Package: me.liuqingwen.android.projectmaterialanimation in project: ProjectMaterialAnimation
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class DetailFragment: BasicFragment()
{
    companion object
    {
        private const val ARG_MOVIE = "arg_movie"
        fun newInstance(movie: Movie) = DetailFragment().apply { this.arguments = bundleOf(DetailFragment.ARG_MOVIE to movie) }
    }
    
    private lateinit var movie: Movie
    private lateinit var layoutConstraint:ConstraintLayout
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = DetailUI().createView(UI{})
    
    override fun onStart()
    {
        super.onStart()
        this.setupUI()
    }
    
    private fun setupUI()
    {
        this.layoutConstraint = this.find(ID_LAYOUT_CONSTRAINT)
        this.bindMovie(this.movie)
        this.bindAnimation()
    }
    
    private fun bindAnimation()
    {
        val constraintSetHide = ConstraintSet().also { it.clone(this.layoutConstraint) }
        val constraintSetShow = ConstraintSet().apply{
            this.connect(ID_IMAGE_MOVIE, ConstraintSet.TOP, PARENT_ID, ConstraintSet.TOP)
            this.connect(ID_IMAGE_MOVIE, ConstraintSet.START, PARENT_ID, ConstraintSet.START)
            this.connect(ID_IMAGE_MOVIE, ConstraintSet.END, PARENT_ID, ConstraintSet.END)
            this.connect(ID_IMAGE_MOVIE, ConstraintSet.BOTTOM, ID_LABEL_DESCRIPTION, ConstraintSet.TOP)
    
            this.constrainWidth(ID_LABEL_TITLE, ConstraintSet.WRAP_CONTENT)
            this.constrainHeight(ID_LABEL_TITLE, ConstraintSet.WRAP_CONTENT)
            this.connect(ID_LABEL_TITLE, ConstraintSet.TOP, PARENT_ID, ConstraintSet.TOP, dip(16))
            this.connect(ID_LABEL_TITLE, ConstraintSet.START, PARENT_ID, ConstraintSet.START)
    
            this.constrainWidth(ID_LABEL_DESCRIPTION, ConstraintSet.MATCH_CONSTRAINT)
            this.constrainHeight(ID_LABEL_DESCRIPTION, ConstraintSet.WRAP_CONTENT)
            this.connect(ID_LABEL_DESCRIPTION, ConstraintSet.BOTTOM, PARENT_ID, ConstraintSet.BOTTOM)
            this.connect(ID_LABEL_DESCRIPTION, ConstraintSet.START, PARENT_ID, ConstraintSet.START)
            this.connect(ID_LABEL_DESCRIPTION, ConstraintSet.END, PARENT_ID, ConstraintSet.END)
            
            this.constrainWidth(ID_LABEL_TAP, ConstraintSet.WRAP_CONTENT)
            this.constrainHeight(ID_LABEL_TAP, ConstraintSet.WRAP_CONTENT)
            this.connect(ID_LABEL_TAP, ConstraintSet.TOP, ID_LABEL_DESCRIPTION, ConstraintSet.BOTTOM)
            this.connect(ID_LABEL_TAP, ConstraintSet.START, PARENT_ID, ConstraintSet.START)
            this.connect(ID_LABEL_TAP, ConstraintSet.END, PARENT_ID, ConstraintSet.END)
        }
        val transition = ChangeBounds().apply {
            this.interpolator = AnticipateOvershootInterpolator(1.0f)
            this.duration = 800
        }
    
        val imageMovie = this.find<ImageView>(ID_IMAGE_MOVIE)
        super.glideRequest.load(this.movie.images.largeImage).into(imageMovie)
        
        var isShowDetail = false
        imageMovie.setOnClickListener {
            if (isShowDetail)
            {
                TransitionManager.beginDelayedTransition(this.layoutConstraint, transition)
                constraintSetHide.applyTo(this.layoutConstraint)
            }
            else
            {
                TransitionManager.beginDelayedTransition(this.layoutConstraint, transition)
                constraintSetShow.applyTo(this.layoutConstraint)
            }
            isShowDetail = ! isShowDetail
        }
    }
    
    private fun bindMovie(movie: Movie)
    {
        val labelTitle = this.find<TextView>(ID_LABEL_TITLE)
        val labelInfo = this.find<TextView>(ID_LABEL_DESCRIPTION)
        
        labelTitle.text = movie.originalTitle
        val info = """Title: ${movie.TranslationTitle} (${movie.year})
            |By: ${movie.movieDirectors.joinToString { it.name }}
            |Stars: ${movie.movieStars.joinToString { it.name }}
            |Genres: ${movie.genres.joinToString()}
            |Link: ${movie.webUrl}
        """.trimMargin()
        labelInfo.text = info
    }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.arguments?.let { this.movie = it.getSerializable(DetailFragment.ARG_MOVIE) as Movie }
        this.enterTransition = Slide(Gravity.BOTTOM)
        this.sharedElementEnterTransition = TransitionSet().apply {
            this.startDelay = 200
            this.ordering = TransitionSet.ORDERING_TOGETHER
            this.addTransition(ChangeBounds()).addTransition(ChangeTransform()).addTransition(ChangeImageTransform())
        }
    }
}

private const val ID_LAYOUT_CONSTRAINT = 0x21
private const val ID_LABEL_TITLE = 0x22
private const val ID_LABEL_DESCRIPTION = 0x23
private const val ID_IMAGE_MOVIE = 0x24
private const val ID_LABEL_TAP = 0x25
class DetailUI:AnkoComponent<Fragment>
{
    override fun createView(ui: AnkoContext<Fragment>) = with(ui) {
        constraintLayout {
            id = ID_LAYOUT_CONSTRAINT
            
            imageView {
                id = ID_IMAGE_MOVIE
                imageResource = R.drawable.placeholder
                scaleType = ImageView.ScaleType.CENTER_CROP
                transitionName = SHARED_TRANSITION_NAME
            }.lparams(width = matchConstraint, height = matchConstraint)
            
            textView {
                id = ID_LABEL_TITLE
                textSize = 24.0f
                textColor = Color.WHITE
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                backgroundDrawable = Color.DKGRAY.toDrawable()
                leftPadding = dip(12)
                rightPadding = dip(12)
                topPadding = dip(4)
                bottomPadding = dip(4)
            }.lparams(width = wrapContent, height = wrapContent)
    
            textView("Tap for detail") {
                id = ID_LABEL_TAP
                textColor = Color.RED
            }.lparams(width = wrapContent, height = wrapContent)
            
            textView {
                id = ID_LABEL_DESCRIPTION
                textSize = 18.0f
                textColor = Color.WHITE
                padding = dip(4)
                backgroundDrawable = Color.GRAY.toDrawable()
            }.lparams(width = matchConstraint, height = wrapContent)
        
            applyConstraintSet {
                //clear(ID_LABEL_TITLE, ConstraintSet.START)
                clear(ID_LABEL_DESCRIPTION, ConstraintSet.BOTTOM)
                
                connect(
                        TOP of ID_LABEL_TITLE to TOP of PARENT_ID margin dip(16),
                        END of ID_LABEL_TITLE to START of ID_IMAGE_MOVIE,
                        
                        START of ID_IMAGE_MOVIE to START of PARENT_ID,
                        END of ID_IMAGE_MOVIE to END of PARENT_ID,
                        TOP of ID_IMAGE_MOVIE to TOP of PARENT_ID,
                        BOTTOM of ID_IMAGE_MOVIE to BOTTOM of PARENT_ID,

                        TOP of ID_LABEL_DESCRIPTION to BOTTOM of ID_IMAGE_MOVIE,
                        START of ID_LABEL_DESCRIPTION to START of PARENT_ID,
                        END of ID_LABEL_DESCRIPTION to END of PARENT_ID,
        
                        START of ID_LABEL_TAP to START of PARENT_ID,
                        END of ID_LABEL_TAP to END of PARENT_ID,
                        BOTTOM of ID_LABEL_TAP to BOTTOM of PARENT_ID margin dip(8)
                       )
            }
        }
    }
}