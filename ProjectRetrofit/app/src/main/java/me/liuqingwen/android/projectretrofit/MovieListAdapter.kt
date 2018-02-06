package me.liuqingwen.android.projectretrofit

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find

/**
 * Created by Qingwen on 2018-2-5, project: ProjectRetrofit.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-5
 * @Package: me.liuqingwen.android.projectretrofit in project: ProjectRetrofit
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class MovieListHolder(private val context: Context, itemView: View): RecyclerView.ViewHolder(itemView)
{
    fun bind(movie: Movie, request: RequestBuilder<Drawable>? = null, action: ((Movie) -> Unit)? = null)
    {
        val movieImage = itemView.find<ImageView>(ID_IMAGE_VIEW)
        request?.load(movie.images.smallImage)?.into(movieImage)
        action?.run { movieImage.setOnClickListener { this.invoke(movie) } }
    }
}

class MovieListAdapter(private val movieList: List<Movie>, var listColumnCount: Int = 1, private val request: RequestBuilder<Drawable>? = null, val onItemClickListener: ((Movie) -> Unit)? = null):
        RecyclerView.Adapter<MovieListHolder>(), ListPreloader.PreloadModelProvider<Movie>
{
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = MovieListHolder(parent!!.context, MovieListItemUI(this.listColumnCount).createView(AnkoContext.create(parent.context, parent)))
    override fun onBindViewHolder(holder: MovieListHolder?, position: Int) = holder?.bind(this.movieList[position], this.request, this.onItemClickListener) ?: Unit
    override fun getItemCount() = this.movieList.size
    
    override fun getPreloadItems(position: Int) = this.movieList.slice(position until position + 1)
    override fun getPreloadRequestBuilder(image: Movie) = request
}
