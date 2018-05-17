package me.liuqingwen.android.projectservice

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Qingwen on 2018-5-14, project: ProjectService.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-14
 * @Package: me.liuqingwen.android.projectservice in project: ProjectService
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

private const val DOUBAN_BASE_URL = "https://api.douban.com/"

object APIService
{
    private val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(DOUBAN_BASE_URL)
            .build()
    private val movieService = this.retrofit.create(IMovieService::class.java)
    
    fun getTopObservable(start:Int = 0, count:Int = 20) = this.movieService.getTop250(start = start, count = count)
    fun getOnShowObservable(city: String) = this.movieService.getOnShowByCity(city = city)
    fun getComingSoonObservable(start: Int = 0, count: Int = 20) = this.movieService.getComingSoonOnes(start = start, count = count)
}

interface IMovieService
{
    @GET("v2/movie/top250")
    fun getTop250(@Query("start") start:Int, @Query("count") count:Int): Observable<MovieResponse>
    
    @GET("/v2/movie/in_theaters")
    fun getOnShowByCity(@Query("city") city: String = "北京"): Observable<MovieResponse>
    
    @GET("/v2/movie/coming_soon")
    fun getComingSoonOnes(@Query("start") start: Int, @Query("count") count: Int): Observable<MovieResponse>
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
