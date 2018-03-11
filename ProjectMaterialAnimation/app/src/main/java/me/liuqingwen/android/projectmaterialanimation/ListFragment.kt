package me.liuqingwen.android.projectmaterialanimation

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.constraint.ConstraintSet.PARENT_ID
import android.support.transition.Slide
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.graphics.drawable.toDrawable
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.google.gson.Gson
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlinx.coroutines.experimental.android.UI as KUI

/**
 * Created by Qingwen on 2018-3-10, project: ProjectMaterialAnimation.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-10
 * @Package: me.liuqingwen.android.projectmaterialanimation in project: ProjectMaterialAnimation
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class ListFragment: BasicFragment(), AnkoLogger
{
    interface IFragmentInteractionListener
    {
        fun onMovieItemSelect(itemImage: View, movie: Movie)
    }
    
    companion object
    {
        private const val COLUMN_COUNT = 3
        fun newInstance() = ListFragment()
    }
    
    private var start = 0
    
    private var fragmentInteractionListener: IFragmentInteractionListener? = null
    private lateinit var layoutSwipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private val dataList by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<Movie>() }
    private val adapter by lazy(LazyThreadSafetyMode.NONE) { MovieListAdapter(this.dataList, ListFragment.COLUMN_COUNT, this.glideRequest) {image, movie->
        this.fragmentInteractionListener?.onMovieItemSelect(image, movie)
    } }
    private val job by lazy(LazyThreadSafetyMode.NONE) { Job() }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = UI {
        layoutSwipeRefresh = swipeRefreshLayout {
            recyclerView = recyclerView {
                adapter = this@ListFragment.adapter
                layoutManager = GridLayoutManager(this.context, ListFragment.COLUMN_COUNT) //every time veiw created should use new layout manager
            }
        }
    }.view
    
    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        this.fragmentInteractionListener = context as? IFragmentInteractionListener
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
    
        this.enterTransition = Slide(Gravity.END)
        this.exitTransition = Slide(Gravity.START)
        
        this.layoutSwipeRefresh.setOnRefreshListener {
            this.loadMovieList()
        }
        
        this.loadMovieList(false)
    }
    
    private fun loadMovieList(isForce: Boolean = true)
    {
        //Maybe the fragment is recreated, make sure no loading
        if (! isForce && this.dataList.isNotEmpty())
        {
            return
        }
        
        this@ListFragment.layoutSwipeRefresh.isRefreshing = true
        
        val job = async(this.job + CommonPool) {
            val url = URL("https://api.douban.com/v2/movie/top250?start=$start&count=15")
            val connection = url.openConnection() as HttpsURLConnection
            if (connection.responseCode == 200)
                Gson().fromJson(connection.inputStream.bufferedReader(), MovieResponse::class.java).subjects
            else
                emptyList<Movie>()
        }
        
        launch(this.job + KUI) {
            val movies = job.await()
            
            this@ListFragment.start += movies.size
            this@ListFragment.dataList += movies
            this@ListFragment.adapter.notifyDataSetChanged()
            this@ListFragment.layoutSwipeRefresh.isRefreshing = false
        }
    }
    
    override fun onDetach()
    {
        super.onDetach()
        this.job.cancel()
    }
}



class MovieListHolder(itemView: View): RecyclerView.ViewHolder(itemView)
{
    fun bind(movie: Movie, request: RequestBuilder<Drawable>? = null, action: ((View, Movie) -> Unit)? = null)
    {
        val movieImage = itemView.find<ImageView>(ID_ITEM_IMAGE_VIEW)
        request?.load(movie.images.smallImage)?.into(movieImage)
        val movieTitle = itemView.find<TextView>(ID_ITEM_LABEL_TITLE)
        movieTitle.text = movie.TranslationTitle
        action?.run { movieImage.setOnClickListener { this.invoke(itemView, movie) } }
    }
}

class MovieListAdapter(private val movieList: List<Movie>, var listColumnCount: Int = 1, private val request: RequestBuilder<Drawable>? = null, val onItemClickListener: ((View, Movie) -> Unit)? = null):
        RecyclerView.Adapter<MovieListHolder>(), ListPreloader.PreloadModelProvider<Movie>
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MovieListHolder(MovieListItemUI(this.listColumnCount).createView(AnkoContext.create(parent.context, parent)))
    override fun onBindViewHolder(holder: MovieListHolder, position: Int) = holder.bind(this.movieList[position], this.request, this.onItemClickListener) ?: Unit
    override fun getItemCount() = this.movieList.size
    
    override fun getPreloadItems(position: Int) = this.movieList.slice(position until position + 1)
    override fun getPreloadRequestBuilder(image: Movie) = request
}

private const val ID_ITEM_CARD_VIEW = 0x01
private const val ID_ITEM_IMAGE_VIEW = 0x02
private const val ID_ITEM_LABEL_TITLE = 0x03
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
    
            val labelTitle = textView {
                id = ID_ITEM_LABEL_TITLE
                padding = dip(6)
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textSize = 16.0f
                textColor = Color.WHITE
                backgroundDrawable = Color.argb(0x80, 0x35, 0xCD, 0xFF).toDrawable()
                translationZ = 20.0f
            }.lparams(width = matchConstraint, height = wrapContent)
            
            applyConstraintSet {
                cardView {
                    connect(START to START of PARENT_ID, END to END of PARENT_ID, TOP to TOP of PARENT_ID)
                    dimensionRation = "w,40:27"
                }
                labelTitle {
                    connect(START to START of cardView, END to END of cardView, BOTTOM to BOTTOM of cardView margin dip(16))
                }
            }
        }
    }
}