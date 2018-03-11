package me.liuqingwen.android.projectmaterialanimation

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Qingwen on 2018-3-10, project: ProjectMaterialAnimation.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-10
 * @Package: me.liuqingwen.android.projectmaterialanimation in project: ProjectMaterialAnimation
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

data class MovieResponse(val count: Int, val start: Int, val total: Int, val title: String, val subjects: List<Movie>)

data class Movie(val id: String, val year: String, val images: SizedImage, val genres: List<String>,
                 @SerializedName("original_title") val originalTitle: String,
                 @SerializedName("title") val TranslationTitle: String,
                 @SerializedName("alt") val webUrl: String,
                 @SerializedName("casts") val movieStars: List<MovieStar>,
                 @SerializedName("directors") val movieDirectors: List<MovieStar>):Serializable
//aka: List<String>, ratings_count: Int, comments_count: Int, subtype: String, summary: String, current_season: Object,
// collect_count: Int, countries: List<String>, episodes_count: Object, schedule_url: String, seasons_count: Object, share_url: String, do_count: Object,
// , douban_site: String, wish_count: Int, reviews_count: Int, rating: Rating(val max:Int, val average:Int, val stars:String, val min:Int)

data class MovieStar(val id: String, val name: String,
                     @SerializedName("alt") val link: String,
                     @SerializedName("avatars") val image: SizedImage):Serializable

data class SizedImage(@SerializedName("small") val smallImage: String,
                      @SerializedName("medium") val mediumImage: String,
                      @SerializedName("large") val largeImage: String):Serializable