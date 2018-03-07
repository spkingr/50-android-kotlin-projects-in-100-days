package me.liuqingwen.android.projectobjectbox

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

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
    private lateinit var appContext:Context
    
    @Before
    fun initAppContext()
    {
        this.appContext = InstrumentationRegistry.getTargetContext()
    }
    
    @Test
    fun useAppContext()
    {
        // Context of the app under test.
        assertEquals("me.liuqingwen.android.projectobjectbox", appContext.packageName)
    }
    
    @Test
    fun testObjectBox()
    {
        val objectBox = (this.appContext.applicationContext as MyApplication).objectBoxStore
        val contactBox = objectBox.boxFor(
                Contact::class.java)
        val count = contactBox.count()
        contactBox.put(
                Contact(0, "Temp1", "123456+111", Date(), "Hunan, china", "http://liuqingwen.me/upload/images/android/zhuanjia.jpg"))
        assertEquals(contactBox.count() , count + 1)
    }
}
