package me.liuqingwen.android.projectobjectbox

import android.os.Parcel
import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.NameInDb
import java.util.*

/**
 * Created by Qingwen on 2018-3-2, project: ProjectObjectBox.
 *
 * @Author: Qingwen
 * @DateTime: 2018-3-2
 * @Package: me.liuqingwen.android.projectobjectbox.model in project: ProjectObjectBox
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

@Entity
data class Contact(@Id var id:Long = 0,
                   var name:String,
                   var phone:String,
                   var birthday: Date,
                   var address:String,
                   @NameInDb("profile_url")
                   var profile:String,
                   @NameInDb("is_star_contact")
                   var isStarContact:Boolean = false,
                   @NameInDb("information")
                   var info:String? = null): Parcelable
{
    constructor(parcel: Parcel) : this(parcel.readLong(),
                                       parcel.readString(),
                                       parcel.readString(),
                                       Date(parcel.readLong()),
                                       parcel.readString(),
                                       parcel.readString(),
                                       parcel.readByte() != 0.toByte(),
                                       parcel.readString())
    
    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeLong(this.id)
        parcel.writeString(this.name)
        parcel.writeString(this.phone)
        parcel.writeLong(this.birthday.time)
        parcel.writeString(this.address)
        parcel.writeString(this.profile)
        parcel.writeByte(if (this.isStarContact) 1 else 0)
        parcel.writeString(this.info)
    }
    
    override fun describeContents(): Int = 0
    companion object CREATOR : Parcelable.Creator<Contact>
    {
        override fun createFromParcel(parcel: Parcel) = Contact(
                parcel)
        override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)
    }
}