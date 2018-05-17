package me.liuqingwen.android.projectservice

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.constraint.Barrier
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestBuilder
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.guideline
import org.jetbrains.anko.constraint.layout.matchConstraint

/**
 * Created by Qingwen on 2018-5-15, project: ProjectService.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-15
 * @Package: me.liuqingwen.android.projectservice in project: ProjectService
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class CustomViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
{
    fun bind(movie: Movie, request: RequestBuilder<Drawable>?)
    {
        val textTitle = this.itemView.find<TextView>(CustomItemUI.ID_TEXT_TITLE)
        val textType = this.itemView.find<TextView>(CustomItemUI.ID_TEXT_TYPE)
        val textDirectors = this.itemView.find<TextView>(CustomItemUI.ID_TEXT_DIRECTORS)
        val textActors = this.itemView.find<TextView>(CustomItemUI.ID_TEXT_ACTORS)
        val textUrl = this.itemView.find<TextView>(CustomItemUI.ID_TEXT_URL)
        
        textTitle.text = "${movie.TranslationTitle}(${movie.originalTitle})"
        textType.text = movie.genres.joinToString(separator = " / ")
        textDirectors.text = movie.movieDirectors.joinToString(separator = " / ") { it.name }
        textActors.text = movie.movieStars.joinToString(separator = " / ") { it.name }
        textUrl.text = movie.webUrl
    
        //????????????????????????
        textUrl.setOnClickListener { this.itemView.context.browse(movie.webUrl) }
        //????????????????????????
        
        val imageMovie = this.itemView.find<ImageView>(CustomItemUI.ID_IMAGE_MOVIE)
        request?.load(movie.images.smallImage)?.into(imageMovie)
    }
}

class CustomAdapter(private val dataList:List<Movie>, private val request:RequestBuilder<Drawable>? = null):RecyclerView.Adapter<CustomViewHolder>()
{
    override fun getItemCount() = this.dataList.size
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CustomViewHolder(CustomItemUI().createView(AnkoContext.create(parent.context, parent)))
    
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) = holder.bind(this.dataList[position], this.request)
}

class CustomItemUI:AnkoComponent<ViewGroup>
{
    companion object
    {
        const val ID_GUIDELINE = 0x20
        const val ID_BARRIER = 0x21
        const val ID_VIEW_SPACE = 0x22
        const val ID_IMAGE_MOVIE = 0x10
        const val ID_CARD_MOVIE = 0x11
        const val ID_TEXT_TITLE = 0x12
        const val ID_TEXT_DIRECTORS = 0x13
        const val ID_TEXT_ACTORS = 0x14
        const val ID_TEXT_URL = 0x15
        const val ID_TEXT_TYPE = 0x16
    }
    
    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui){
        constraintLayout {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            
            guideline {
                id = ID_GUIDELINE
            }.lparams(width = matchConstraint, height = matchConstraint){
                orientation = ConstraintLayout.LayoutParams.VERTICAL
                guidePercent = 0.25f
            }
            
            cardView {
                id = ID_CARD_MOVIE
                imageView {
                    id = ID_IMAGE_MOVIE
                    imageResource = R.drawable.placeholder
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }.lparams(width = matchParent, height = matchParent)
            }.lparams(width = matchConstraint, height = matchConstraint)
            
            textView {
                id = ID_TEXT_TITLE
                textSize = 18.0f
                textColor = Color.MAGENTA
            }.lparams(width = matchConstraint, height = wrapContent)
    
            textView {
                id = ID_TEXT_TYPE
            }.lparams(width = matchConstraint, height = wrapContent)
    
            textView {
                id = ID_TEXT_DIRECTORS
            }.lparams(width = matchConstraint, height = wrapContent)
    
            textView {
                id = ID_TEXT_ACTORS
            }.lparams(width = matchConstraint, height = wrapContent)
    
            textView {
                id = ID_TEXT_URL
                textSize = 12.0f
                textColor = Color.BLUE
            }.lparams(width = matchConstraint, height = wrapContent)
            
            view {
                id = ID_VIEW_SPACE
                backgroundColor = Color.LTGRAY
            }.lparams(width = matchConstraint, height = dip(2))
            
            applyConstraintSet {
                setDimensionRatio(ID_CARD_MOVIE, "27:40")
                createBarrier(ID_BARRIER, Barrier.BOTTOM, ID_CARD_MOVIE, ID_TEXT_URL)
                
                connect(
                        START of ID_CARD_MOVIE to START of PARENT_ID margin dip(4),
                        END of ID_CARD_MOVIE to START of ID_GUIDELINE margin dip(4),
                        TOP of ID_CARD_MOVIE to TOP of PARENT_ID margin dip(8),
        
                        START of ID_TEXT_TITLE to END of ID_GUIDELINE margin dip(4),
                        END of ID_TEXT_TITLE to END of PARENT_ID margin dip(4),
                        TOP of ID_TEXT_TITLE to TOP of PARENT_ID margin dip(4),
        
                        START of ID_TEXT_TYPE to START of ID_TEXT_TITLE,
                        END of ID_TEXT_TYPE to END of PARENT_ID margin dip(4),
                        TOP of ID_TEXT_TYPE to BOTTOM of ID_TEXT_TITLE margin dip(4),
        
                        START of ID_TEXT_DIRECTORS to START of ID_TEXT_TYPE,
                        END of ID_TEXT_DIRECTORS to END of PARENT_ID margin dip(4),
                        TOP of ID_TEXT_DIRECTORS to BOTTOM of ID_TEXT_TYPE margin dip(4),
        
                        START of ID_TEXT_ACTORS to START of ID_TEXT_DIRECTORS,
                        END of ID_TEXT_ACTORS to END of PARENT_ID margin dip(4),
                        TOP of ID_TEXT_ACTORS to BOTTOM of ID_TEXT_DIRECTORS margin dip(4),
        
                        START of ID_TEXT_URL to START of ID_TEXT_ACTORS,
                        END of ID_TEXT_URL to END of PARENT_ID margin dip(4),
                        TOP of ID_TEXT_URL to BOTTOM of ID_TEXT_ACTORS margin dip(4),
                        
                        START of ID_VIEW_SPACE to START of PARENT_ID,
                        END of ID_VIEW_SPACE to END of PARENT_ID,
                        TOP of ID_VIEW_SPACE to TOP of ID_BARRIER margin dip(4)
                       )
            }
        }
    }
}