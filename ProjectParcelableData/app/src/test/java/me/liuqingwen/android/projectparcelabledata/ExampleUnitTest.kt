package me.liuqingwen.android.projectparcelabledata

import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest
{
    @Test
    fun addition_isCorrect()
    {
        //INSERT INTO contact VALUES (2, "David", "010-123456", 883584000000, "USA, Somewhere, Road No.123456789000000000", "http://liuqingwen.me/upload/images/android/David.jpg", 1, "No more information.\nYou can leave something here.");
        var date = Date()
        println(date.time)
        println(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date))
        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("1998-01-01") //567964800000
        println(date.time)
        println(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date))
        assertEquals(4, 2 + 2)
    }
}
