package me.liuqingwen.android.projectparcelabledata

import android.arch.persistence.room.*
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Qingwen on 2017-2017-12-23, project: ProjectParcelableData.
 *
 * @Author: Qingwen
 * @DateTime: 2017-12-23
 * @Package: me.liuqingwen.android.projectparcelabledata in project: ProjectParcelableData
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class DatabaseHelper private constructor(context: Context)
{
    companion object
    {
        @Volatile
        private var INSTANCE:DatabaseHelper? = null
        fun getInstance(context: Context) = if (INSTANCE == null) {
            synchronized(DatabaseHelper::class) {
                INSTANCE = DatabaseHelper(context)
                INSTANCE!!
            }
        } else INSTANCE!!
    }
    
    private val appDatabase by lazy(LazyThreadSafetyMode.NONE) { Room.databaseBuilder(context, AppDatabase::class.java, "database.db").allowMainThreadQueries().build() }
    private val contactDao by lazy(LazyThreadSafetyMode.NONE) { this.appDatabase.contactDao() }
    
    fun getContactById(id:Long) = this.contactDao.findContactById(id)
    fun getAllContacts() = this.contactDao.findAllContacts()
    fun addContacts(vararg contact:Contact) = this.contactDao.insertContacts(*contact)
    fun modifyContacts(vararg contact:Contact) = this.contactDao.updateContacts(*contact)
    fun removeContacts(vararg contact:Contact) = this.contactDao.deleteContacts(*contact)
}

@Entity(tableName = "contact")
data class Contact(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id:Long = 0,
                   @ColumnInfo(name = "name") var name:String = "",
                   @ColumnInfo(name = "phone") var phone:String = "",
                   @ColumnInfo(name = "birthday") var birthday: Long = 0,
                   @ColumnInfo(name = "address") var address:String = "",
                   @ColumnInfo(name = "profile") var profile:String = "",
                   @ColumnInfo(name = "is_star") var isStarContact:Boolean = false,
                   @ColumnInfo(name = "information") var info:String? = null): Parcelable
{
    constructor(parcel: Parcel) : this(parcel.readLong(),
                                       parcel.readString(),
                                       parcel.readString(),
                                       parcel.readLong(),
                                       parcel.readString(),
                                       parcel.readString(),
                                       parcel.readByte() != 0.toByte(),
                                       parcel.readString())
    
    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeLong(this.id)
        parcel.writeString(this.name)
        parcel.writeString(this.phone)
        parcel.writeLong(this.birthday)
        parcel.writeString(this.address)
        parcel.writeString(this.profile)
        parcel.writeByte(if (this.isStarContact) 1 else 0)
        parcel.writeString(this.info)
    }
    
    override fun describeContents(): Int = 0
    companion object CREATOR : Parcelable.Creator<Contact>
    {
        override fun createFromParcel(parcel: Parcel) = Contact(parcel)
        override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)
    }
}

@Dao interface ContactDao
{
    @Query("SELECT * FROM contact WHERE id = :arg0")
    fun findContactById(id:Long):Contact?
    @Query("SELECT * FROM contact")
    fun findAllContacts():List<Contact>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContacts(vararg contact:Contact)
    
    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updateContacts(vararg contact:Contact)
    
    @Delete
    fun deleteContacts(vararg contact:Contact)
}

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase()
{
    abstract fun contactDao():ContactDao
}
