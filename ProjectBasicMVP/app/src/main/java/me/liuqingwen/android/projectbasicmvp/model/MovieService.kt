package me.liuqingwen.android.projectbasicmvp.model

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Qingwen on 2018-2-17, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-17
 * @Package: me.liuqingwen.android.projectbasicmvp.model in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

private const val DOUBAN_BASE_URL = "https://api.douban.com/"

object APIMovieService
{
    private val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(DOUBAN_BASE_URL)
            .build()
    private val movieService = retrofit.create(MovieService::class.java)
    
    fun getMovieList(start: Int = 0, count: Int = 20, onSuccess: ((List<Movie>)->Unit)? = null, onFailure: ((Throwable)->Unit)? = null) = this.movieService
            .getTop250(start, count)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onSuccess?.invoke(it.subjects) }, { onFailure?.invoke(it) })!!
    
    fun searchMovie(text: String, tag: String = "", start: Int = 0, count: Int = 20, onSuccess: ((List<Movie>)->Unit)? = null, onFailure: ((Throwable)->Unit)? = null) = this.movieService
            .searchMovies(text, tag, start, count)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onSuccess?.invoke(it.subjects) }, { onFailure?.invoke(it) })!!
}

interface MovieService
{
    @GET("/v2/movie/subject/{id}")
    fun getMovieInfo(@Path("id") movieId: String): Observable<Movie>
    
    @GET("/v2/movie/search")
    fun searchMovies(@Query("q") text: String, @Query("tag") tag: String , @Query("start") start: Int, @Query("count") count: Int): Observable<MovieResponse>
    
    @GET("v2/movie/top250")
    fun getTop250(@Query("start") start:Int, @Query("count") count:Int): Observable<MovieResponse>
    
    @GET("/v2/movie/in_theaters")
    fun getOnShowByCity(@Query("city") city: String = "北京"): Observable<MovieResponse>
    
    @GET("/v2/movie/coming_soon")
    fun getComingSoonOnes(@Query("start") start: Int = 0, @Query("count") count: Int = 20): Observable<MovieResponse>
    
    /*@GET("/v2/movie/celebrity/{id}")
    fun getCelebrityInfo(@Path("id") celebrityId: String)
    @GET("/v2/movie/subject/{id}/photos")
    fun getMoviePhotos(@Path("id") movieId: String)
    @GET("/v2/movie/subject/{id}/reviews")
    fun getMovieReviews(@Path("id") movieId: String)
    @GET("/v2/movie/subject/{id}/comments")
    fun getMovieComments(@Path("id") movieId: String)
    @GET("/v2/movie/celebrity/{id}/works")
    fun getMoviesByCelebrity(@Path("id") celebrityId: String)
    @GET("/v2/movie/celebrity/{id}/photos")
    fun getPhotosOfCelebrity(@Path("id") celebrityId: String)
    
    @GET("/v2/movie/us_box")
    fun getUSTopList(): Observable<Subject(val date:String, val title:String, val subjects:List<Movie>)>
    @GET("/v2/movie/weekly")
    fun getTopWeekly()
    @GET("/v2/movie/new_movies")
    fun getLatest()*/
}

data class MovieResponse(val count: Int, val start: Int, val total: Int, val title: String, val subjects: List<Movie>)

data class Movie(val id: String, val year: String, val images: SizedImage, val genres: List<String>,
                 @SerializedName("original_title") val originalTitle: String,
                 @SerializedName("title") val TranslationTitle: String,
                 @SerializedName("alt") val webUrl: String,
                 @SerializedName("casts") val movieStars: List<MovieStar>,
                 @SerializedName("directors") val movieDirectors: List<MovieStar>)
//aka: List<String>, ratings_count: Int, comments_count: Int, subtype: String, summary: String, current_season: Object,
// collect_count: Int, countries: List<String>, episodes_count: Object, schedule_url: String, seasons_count: Object, share_url: String, do_count: Object,
// , douban_site: String, wish_count: Int, reviews_count: Int, rating: Rating(val max:Int, val average:Int, val stars:String, val min:Int)

data class MovieStar(val id: String, val name: String,
                     @SerializedName("alt") val link: String,
                     @SerializedName("avatars") val image: SizedImage)

data class SizedImage(@SerializedName("small") val smallImage: String,
                      @SerializedName("medium") val mediumImage: String,
                      @SerializedName("large") val largeImage: String)