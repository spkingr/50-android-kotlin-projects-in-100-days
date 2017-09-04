package me.liuqingwen.android.projectdatabaseroom

import android.arch.persistence.room.*
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Qingwen on 2017-9-3.
 */

@Entity(tableName = "post")
@TypeConverters(Converters::class)
data class Post(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true)
                var id:Long,
                @ColumnInfo(name = "title")
                var title:String = "",
                @ColumnInfo(name = "url")
                var url:String = "",
                @ColumnInfo(name = "content")
                var content:String = "",
                @ColumnInfo(name = "creation_date")
                var creationDate:Date? = null) {
    constructor():this(0)
}

class Converters()
{
    @TypeConverter
    fun dateToString(date:Date?):String? = date?.let { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(it) }
    
    @TypeConverter
    fun stringToDate(dateString:String?):Date? = dateString?.let { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it) }
}

@Dao interface PostDao
{
    @Query("SELECT * FROM  post")
    fun getAllPost():List<Post>
    
    @Query("SELECT * FROM post WHERE id = :arg0")
    fun getPostById(id:Long):Post?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPost(post:Post):Long
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addPosts(vararg post:Post)
    
    @Delete
    fun deletePost(post:Post)
}

@Database(entities = arrayOf(Post::class), version = 1, exportSchema = false)
abstract class AppDatabase:RoomDatabase()
{
    abstract fun postDao():PostDao
}