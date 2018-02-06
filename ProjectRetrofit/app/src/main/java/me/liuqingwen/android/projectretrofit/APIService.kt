package me.liuqingwen.android.projectretrofit

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Qingwen on 2018-2-5, project: ProjectRetrofit.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-5
 * @Package: me.liuqingwen.android.projectretrofit in project: ProjectRetrofit
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
    private val movieService = retrofit.create(MovieService::class.java)
    
    fun getMovieListObservable(start: Int = 0, count: Int = 20) = this.movieService.getTop250(start, count)
}

interface MovieService
{
    @GET("/v2/movie/subject/{id}")
    fun getMovieInfo(@Path("id") movieId: String): Observable<Movie>
    
    @GET("/v2/movie/search")
    fun searchMovies(@Query("q") text: String, @Query("tag") tag: String = "", @Query("start") start: Int = 0, @Query("count") count: Int = 20): Observable<MovieResponse>
    
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