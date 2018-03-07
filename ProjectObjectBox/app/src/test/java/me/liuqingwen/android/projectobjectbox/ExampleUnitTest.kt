package me.liuqingwen.android.projectobjectbox

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
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date1 = simpleDateFormat.parse("2018-3-2 18:08:30")
        println(date1.time)
        val date2 = Date()
        println(date2.time)
        val date3 = Date(date1.time + 10 * 1000)
        println(simpleDateFormat.format(date3))
        val date4 = Date(date2.time + 24 * 60 * 60 * 1000)
        println(simpleDateFormat.format(date4))
    
        val s = "Donn Felker.jpg".replace(" ", "")
        println(s)
        
        val d1 = Date(0)
        val d2 = Date(0)
        println("d1==d2: ${d1 == d2}")
        
        val date5 = simpleDateFormat.parse("2018-3-2 18:08:30")
        println("date1==date5: ${date1 == date5}")
        
        assertEquals(4, 2 + 2)
    }
}
