package me.liuqingwen.android.projectphotowall

import org.junit.Test

import org.junit.Assert.*
import kotlin.math.roundToInt

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
        println(1.11.roundToInt())
        println(1.6.roundToInt())
        println(Math.round(1.2))
        println(Math.round(1.7))
        assertEquals(4, 2 + 2)
    }
}
