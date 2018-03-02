package me.liuqingwen.android.projectbasicmvp.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.os.Build
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.support.v4.app.Fragment
import android.text.InputType
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import me.liuqingwen.android.projectbasicmvp.R
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.jetbrains.anko.support.v4.viewPager

/**
 * Created by Qingwen on 2018-2-16, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-16
 * @Package: me.liuqingwen.android.projectbasicmvp.ui in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

const val ID_LAYOUT_TOOLBAR = 0x20
const val ID_LIST_MOVIE  = 0x21
const val ID_BUTTON_UP  = 0x22
const val ID_LAYOUT_SWIPE_REFRESH  = 0x23
const val ID_ITEM_CARD_VIEW = 0x24
const val ID_ITEM_IMAGE_VIEW = 0x25
//const val ID_FRAGMENT_CONTAINER = 0x26
const val ID_DIALOG_CARD_VIEW = 0x27
const val ID_DIALOG_LABEL_INFO = 0x28
const val ID_DIALOG_LABEL_TITLE = 0x29
const val ID_DIALOG_IMAGE_MOVIE = 0x30
const val ID_LAYOUT_SEARCH_CONTAINER = 0x31
const val ID_TEXT_SEARCH = 0x32
const val ID_BUTTON_SEARCH = 0x33
const val ID_LAYOUT_TAB = 0x34
const val ID_LAYOUT_VIEWPAGER = 0x35
const val ID_LAYOUT_APPBAR = 0x36

class MovieUI: AnkoComponent<Context>
{
    override fun createView(ui: AnkoContext<Context>) = with(ui) {
        val tabHeight = dip(56)
        
        coordinatorLayout {
            fitsSystemWindows = true
        
            appBarLayout {
                id = ID_LAYOUT_APPBAR
                
                toolbar {
                    id = ID_LAYOUT_TOOLBAR
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        setTitleTextColor(Color.WHITE)
                    }
                }.lparams(width = matchParent, height = wrapContent, weight = 1.0f)
            
            }.lparams(width = matchParent, height = wrapContent)
            
            viewPager {
                id = ID_LAYOUT_VIEWPAGER
                padding = dip(4)
            }.lparams(width = matchParent, height = matchParent)
            
            tabLayout {
                id = ID_LAYOUT_TAB
                backgroundColorResource = R.color.inputBackgroundColor
                setSelectedTabIndicatorHeight(0)
            }.lparams(width = matchParent, height = tabHeight){ gravity = Gravity.BOTTOM }
        
            floatingActionButton {
                id = ID_BUTTON_UP
                imageResource = R.drawable.arrow_up
            }.lparams{ marginEnd = dip(8); rightMargin = dip(8); bottomMargin = tabHeight + dip(8); gravity = Gravity.BOTTOM or Gravity.END }
        }
    }
    
}

/**
 * Top250 movie list fragment
 */
class MovieListViewUI: AnkoComponent<Fragment>
{
    override fun createView(ui: AnkoContext<Fragment>): View = with(ui) {
        swipeRefreshLayout {
            id = ID_LAYOUT_SWIPE_REFRESH
            isRefreshing = false
            
            recyclerView {
                id = ID_LIST_MOVIE
            }
        }
    }
}

/**
 * Movie search fragment
 */
class MovieSearchViewUI: AnkoComponent<Fragment>
{
    override fun createView(ui: AnkoContext<Fragment>): View = with(ui) {
        constraintLayout {
            id = ID_LAYOUT_SEARCH_CONTAINER
            //lparams(width = matchParent, height = matchParent) //?????????
            
            recyclerView {
                id = ID_LIST_MOVIE
            }.lparams(width = matchParent, height = matchParent)
            
            editText {
                id = ID_TEXT_SEARCH
                hint = "Search..."
                ems = 20
                inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                textColorResource = R.color.inputColor
                textSize = 18.0f
            }.lparams(width = matchConstraint, height = wrapContent)
    
            button("Search") {
                id = ID_BUTTON_SEARCH
                transformationMethod = null
            }.lparams(width = wrapContent, height = wrapContent)
            
            applyConstraintSet {
                connect(
                        START of ID_TEXT_SEARCH to START of PARENT_ID margin dip(8),
                        TOP of ID_TEXT_SEARCH to TOP of PARENT_ID margin dip(8),

                        END of ID_TEXT_SEARCH to START of ID_BUTTON_SEARCH margin dip(8),
                        
                        END of ID_BUTTON_SEARCH to END of PARENT_ID margin dip(8),
                        
                        BOTTOM of ID_BUTTON_SEARCH to BOTTOM of ID_TEXT_SEARCH
                       )
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
                id = ID_ITEM_CARD_VIEW
                radius = 20.0f
                
                imageView {
                    id = ID_ITEM_IMAGE_VIEW
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
                id = ID_DIALOG_CARD_VIEW
                radius = 20.0f
                
                imageView {
                    id = ID_DIALOG_IMAGE_MOVIE
                    imageResource = R.drawable.placeholder
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }.lparams(width = wrapContent, height = imageHeight)
            }.lparams(width = wrapContent, height = wrapContent)
            
            textView {
                id = ID_DIALOG_LABEL_TITLE
                typeface = Typeface.DEFAULT_BOLD
                textSize = 24.0f
                textColor = Color.BLACK
            }.lparams(width = wrapContent, height = wrapContent)
            
            textView {
                id = ID_DIALOG_LABEL_INFO
                textSize = 16.0f
            }.lparams(width = matchConstraint, height = wrapContent)
            
            applyConstraintSet {
                connect(
                        TOP of ID_DIALOG_CARD_VIEW to TOP of PARENT_ID margin dip(4),
                        START of ID_DIALOG_CARD_VIEW to START of PARENT_ID,
                        END of ID_DIALOG_CARD_VIEW to END of PARENT_ID,
        
                        TOP of ID_DIALOG_LABEL_TITLE to BOTTOM of ID_DIALOG_CARD_VIEW margin dip(8),
                        START of ID_DIALOG_LABEL_TITLE to START of PARENT_ID margin dip(8),
                        END of ID_DIALOG_LABEL_TITLE to END of PARENT_ID margin dip(8),
        
                        TOP of ID_DIALOG_LABEL_INFO to BOTTOM of ID_DIALOG_LABEL_TITLE margin dip(8),
                        START of ID_DIALOG_LABEL_INFO to START of PARENT_ID margin dip(16),
                        END of ID_DIALOG_LABEL_INFO to END of PARENT_ID margin dip(16)
                       )
            }
            
        }
    }
    
}
