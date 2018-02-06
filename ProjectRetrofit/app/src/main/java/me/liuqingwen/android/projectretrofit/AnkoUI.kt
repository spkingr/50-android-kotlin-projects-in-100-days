package me.liuqingwen.android.projectretrofit

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.os.Build
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.swipeRefreshLayout

/**
 * Created by Qingwen on 2018-2-5, project: ProjectRetrofit.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-5
 * @Package: me.liuqingwen.android.projectretrofit in project: ProjectRetrofit
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

internal const val ID_RECYCLER_VIEW = 0x11
internal const val ID_FRAGMENT_CONTAINER = 0x12
internal const val ID_TOOL_BAR = 0x13
internal const val ID_CARD_VIEW = 0x14
internal const val ID_BUTTON_UP = 0x15
internal const val ID_IMAGE_VIEW = 0x21
internal const val ID_SWIPE_REFRESH = 0x22
internal const val ID_CARD_IMAGE_MOVIE = 0x90
internal const val ID_IMAGE_MOVIE = 0x91
internal const val ID_LABEL_TITLE = 0x92
internal const val ID_LABEL_INFO = 0x93

class MainUI(private var toolbarTitle: String) : AnkoComponent<Context>
{
    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        coordinatorLayout {
            fitsSystemWindows = true
            
            appBarLayout {
                
                toolbar {
                    id = ID_TOOL_BAR
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        setTitleTextColor(Color.WHITE)
                        title = this@MainUI.toolbarTitle
                    }
                }.lparams(width = matchParent, height = wrapContent, weight = 1.0f)
                
            }.lparams(width = matchParent, height = wrapContent)
    
            constraintLayout {
                padding = dip(4)
                id = ID_FRAGMENT_CONTAINER
            }.lparams(width = matchParent, height = matchParent)
            
            floatingActionButton {
                id = ID_BUTTON_UP
                imageResource = R.drawable.arrow_up
            }.lparams{
                marginEnd = dip(8)
                rightMargin = dip(8)
                bottomMargin = dip(8)
                gravity = Gravity.BOTTOM or Gravity.END
            }
        }
    }
}

class MovieListViewUI: AnkoComponent<Fragment>
{
    override fun createView(ui: AnkoContext<Fragment>): View = with(ui) {
        swipeRefreshLayout {
            id = ID_SWIPE_REFRESH
            isRefreshing = false
            
            recyclerView {
                id = ID_RECYCLER_VIEW
            }
        }
    }
}

class MovieListItemUI(private val itemColumn: Int): AnkoComponent<ViewGroup>
{
    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        val outPadding = 4
        val outSize = Point().also { ui.ctx.windowManager.defaultDisplay.getSize(it) }
        val outWidth = outSize.x / this@MovieListItemUI.itemColumn - outPadding * 2
        
        constraintLayout {
            padding = dip(outPadding)
            
            val cardView = cardView{
                id = ID_CARD_VIEW
                radius = 20.0f
                
                imageView {
                    id = ID_IMAGE_VIEW
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }.lparams(width = matchParent, height = matchParent)
                
            }.lparams(width = outWidth, height = matchConstraint)
            
            applyConstraintSet {
                cardView {
                    connect(START to START of PARENT_ID, END to END of PARENT_ID, TOP to TOP of PARENT_ID)
                    dimensionRation = "w,40:27"
                }
            }
        }
    }
}

class MovieDialogUI:AnkoComponent<Context>
{
    override fun createView(ui: AnkoContext<Context>) = with(ui) {
        val outSize = Point().also { ui.ctx.windowManager.defaultDisplay.getSize(it) }
        val imageHeight = outSize.y / 3
        
        constraintLayout {
            
            cardView {
                id = ID_CARD_IMAGE_MOVIE
                radius = 20.0f
                
                imageView {
                    id = ID_IMAGE_MOVIE
                    imageResource = R.drawable.placeholder
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }.lparams(width = wrapContent, height = imageHeight)
            }.lparams(width = wrapContent, height = wrapContent)
    
            textView {
                id = ID_LABEL_TITLE
                typeface = Typeface.DEFAULT_BOLD
                textSize = 24.0f
                textColor = Color.BLACK
            }.lparams(width = wrapContent, height = wrapContent)
    
            textView {
                id = ID_LABEL_INFO
                textSize = 16.0f
            }.lparams(width = matchConstraint, height = wrapContent)
    
            applyConstraintSet {
                connect(
                        TOP of ID_CARD_IMAGE_MOVIE to TOP of PARENT_ID margin dip(4),
                        START of ID_CARD_IMAGE_MOVIE to START of PARENT_ID,
                        END of ID_CARD_IMAGE_MOVIE to END of PARENT_ID,
        
                        TOP of ID_LABEL_TITLE to BOTTOM of ID_CARD_IMAGE_MOVIE margin dip(8),
                        START of ID_LABEL_TITLE to START of PARENT_ID margin dip(8),
                        END of ID_LABEL_TITLE to END of PARENT_ID margin dip(8),
        
                        TOP of ID_LABEL_INFO to BOTTOM of ID_LABEL_TITLE margin dip(8),
                        START of ID_LABEL_INFO to START of PARENT_ID margin dip(16),
                        END of ID_LABEL_INFO to END of PARENT_ID margin dip(16)
                       )
            }
            
        }
    }
    
}