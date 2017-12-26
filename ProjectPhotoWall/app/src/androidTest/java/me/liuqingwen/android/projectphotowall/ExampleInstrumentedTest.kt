package me.liuqingwen.android.projectphotowall

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest
{
    @Test
    fun useAppContext()
    {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("me.liuqingwen.android.projectphotowall", appContext.packageName)
    }
    
    private lateinit var appDatabase:AppDatabase
    private lateinit var photoDao:PhotoDao
    
    @Before
    fun createDb()
    {
        this.appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(), AppDatabase::class.java).build()
        this.photoDao = this.appDatabase.photoDao()
    }
    
    @After
    fun closeDb()
    {
        this.appDatabase.close()
    }
    
    @Test
    fun testDb()
    {
        //INSERT INTO photo VALUES (1, "http://liuqingwen.me/upload/images/android/photowall/photo_wall_1.jpg", "Unknown", "2017-12-25 23:00:00", "金刚狼2");
        //INSERT INTO photo VALUES (2, "http://liuqingwen.me/upload/images/android/photowall/photo_wall_2.jpg", "Unknown", "2017-12-25 23:00:00", "Dorian Gray");
        //INSERT INTO photo VALUES (3, "http://liuqingwen.me/upload/images/android/photowall/photo_wall_3.jpg", "Unknown", "2017-12-25 23:00:00", "王的盛宴");
        //INSERT INTO photo VALUES (4, "http://liuqingwen.me/upload/images/android/photowall/photo_wall_4.jpg", "Unknown", "2017-12-25 23:00:00", "Future X-Cops");
        //INSERT INTO photo VALUES (5, "http://liuqingwen.me/upload/images/android/photowall/photo_wall_5.jpg", "Unknown", "2017-12-25 23:00:00", "Captain America");
        //INSERT INTO photo VALUES (6, "http://liuqingwen.me/upload/images/android/photowall/photo_wall_6.jpg", "Unknown", "2017-12-25 23:00:00", "Angelesy Demonios");
        //....
        //INSERT INTO photo VALUES (7, "http://liuqingwen.me/upload/images/android/photowall/photo_wall_7.jpg", "Unknown", "2017-12-25 23:00:00", "The Last Supper");
        //INSERT INTO photo VALUES (8, "http://liuqingwen.me/upload/images/android/photowall/photo_wall_8.jpg", "Unknown", "2017-12-25 23:00:00", "Sherlock");
        //INSERT INTO photo VALUES (9, "http://liuqingwen.me/upload/images/android/photowall/photo_wall_9.jpg", "Unknown", "2017-12-25 23:00:00", "沙漠之尊");
        var photo = Photo(0, "http://www.g.com/1.jpg", "Unknown", Date(), "No information left here.")
        this.photoDao.insertPhotos(photo)
        photo = Photo(0, "http://www.g.com/2.jpg", "Unknown2", Date(), "No information left here.")
        this.photoDao.insertPhotos(photo)
        photo = Photo(0, "http://www.g.com/3.jpg", "Unknown2", Date(), "No information left here.")
        this.photoDao.insertPhotos(photo)
        val photos = this.photoDao.findAllPhotos()
        assertEquals(photos[2].id, 3)
    }
}
