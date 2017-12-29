package me.liuqingwen.android.projectimageuploader

import com.google.gson.Gson
import org.junit.Test

import org.junit.Assert.*

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
        assertEquals(4, 2 + 2)
    }
    
    @Test
    fun myTest()
    {
        val body = """{"result" : true, "info" : "Login sucessfully!"}"""
        val info = Gson().fromJson<UploadResult>(body, UploadResult::class.java)
        println(info.info)
        println(info.author)
        assertEquals(info.result, true)
    }
}
