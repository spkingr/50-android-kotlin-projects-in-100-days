package me.liuqingwen.android.projectandroidtest.data

/**
 * Created by Qingwen on 2018-5-31, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-31
 * @Package: me.liuqingwen.android.projectandroidtest.data in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

object NotesServiceApiEndPoint
{
    private val data = mutableListOf(
            Note(id = 1, title = "Write Blog", content = """
                Historically, thousands of newcomers used try.kotlinlang.org as an interactive way of learning the language.
                In particular, Kotlin Koans online have been extremely popular. More advanced users use this playground for trying
                small snippets without opening an IDE, for example before pasting code as an answer on StackOverflow.
            """.trimIndent(), image = "http://url.to.host/image.jpg"),
            Note(id = 2, title = "Second One", content = """
                Android Things enables you to build and maintain IoT devices at scale. We recently released
                Android Things 1.0 with long-term support for production devices,
                so you can easily take an IoT device from prototype to commercial product.
            """.trimIndent(), image = "http://url.to.host/image.jpg"),
            Note(id = 3, title = "Untitled", content = """
                Keeping 2 billion Android devices safe with machine learning
            """.trimIndent(), image = "http://url.to.host/image.jpg")
                                    )
    
    fun loadPersistedData() = this.data
}