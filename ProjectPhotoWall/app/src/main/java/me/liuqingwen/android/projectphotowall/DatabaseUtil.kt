package me.liuqingwen.android.projectphotowall

import android.arch.persistence.room.*
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Qingwen on 2017-2017-12-24, project: ProjectPhotoWall.
 *
 * @Author: Qingwen
 * @DateTime: 2017-12-24
 * @Package: me.liuqingwen.android.projectphotowall in project: ProjectPhotoWall
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class AppDatabaseHelper private constructor(context:Context)
{
    companion object
    {
        @Volatile
        private var INSTANCE:AppDatabaseHelper? = null
        fun getInstance(context: Context) = if (INSTANCE == null) {
            synchronized(AppDatabaseHelper::class) {
                INSTANCE = AppDatabaseHelper(context)
                INSTANCE!!
            }
        } else INSTANCE!!
    }
    
    private val appDatabase by lazy(LazyThreadSafetyMode.NONE) { Room.databaseBuilder(context, AppDatabase::class.java, "database.db").build() }
    private val photoDao by lazy(LazyThreadSafetyMode.NONE) { this.appDatabase.photoDao() }
    
    fun getAllPhotos() = this.photoDao.findAllPhotos()
    fun getPhotoById(id:Int) = this.photoDao.findPhotoById(id)
    fun addPhotos(vararg photo: Photo) = this.photoDao.insertPhotos(*photo)
    fun updatePhotos(vararg photo: Photo) = this.photoDao.updatePhotos(*photo)
    fun removePhotos(vararg photo: Photo) = this.photoDao.deletePhotos(*photo)
}

@Entity(tableName = "photo")
@TypeConverters(DatabaseConverter::class)
data class Photo(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id:Long = 0,
                 @ColumnInfo(name = "url") var url:String = "",
                 @ColumnInfo(name = "author") var author:String = "",
                 @ColumnInfo(name = "date") var date:Date? = null,
                 @ColumnInfo(name = "description") var description:String = ""): Parcelable
{
    constructor(parcel: Parcel) : this(parcel.readLong(), parcel.readString(), parcel.readString(), parcel.readLong().let { if (it == 0L) null else Date(it) }, parcel.readString())
    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeLong(id)
        parcel.writeString(url)
        parcel.writeString(author)
        parcel.writeLong(date?.time ?: 0L)
        parcel.writeString(description)
    }
    override fun describeContents() = 0
    companion object CREATOR : Parcelable.Creator<Photo>
    {
        override fun createFromParcel(parcel: Parcel) = Photo(parcel)
        override fun newArray(size: Int): Array<Photo?> = arrayOfNulls(size)
    }
}

class DatabaseConverter
{
    @TypeConverter
    fun dateToString(date:Date?):String? = date?.let { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(it) }
    @TypeConverter
    fun stringToDate(string:String?):Date? = string?.let { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it) }
}

@Dao interface PhotoDao
{
    @Query("SELECT * FROM photo")
    fun findAllPhotos():List<Photo>
    @Query("SELECT * FROM photo WHERE id = :arg0")
    fun findPhotoById(id:Int):Photo?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotos(vararg photo:Photo)
    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updatePhotos(vararg photo:Photo)
    @Delete
    fun deletePhotos(vararg photo:Photo)
}

@Database(entities = [Photo::class], version = 1, exportSchema = false)
abstract class AppDatabase:RoomDatabase()
{
    abstract fun photoDao():PhotoDao
}