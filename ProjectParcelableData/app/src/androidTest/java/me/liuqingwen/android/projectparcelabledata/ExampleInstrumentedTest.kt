package me.liuqingwen.android.projectparcelabledata

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest
{
    private var database:AppDatabase? = null
    private var contactDao:ContactDao? = null
    
    @Test
    fun useAppContext()
    {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("me.liuqingwen.android.projectparcelabledata", appContext.packageName)
    }
    
    @Before
    fun createDb()
    {
        val context = InstrumentationRegistry.getContext()
        this.database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        this.contactDao = this.database!!.contactDao()
    }
    
    @After
    fun closeDb()
    {
        this.database!!.close()
    }
    
    @Test
    fun testDb()
    {
        val c = Contact(1, "Jackson Five", "18800009999" , Date().time, "American, Road No.1234", "http://g.cn/profile.jpg", true, "No information left here.")
        this.contactDao!!.insertContacts(c)
        val cs = this.contactDao!!.findAllContacts()
        assertEquals(cs[0].id, c.id)
    }
}