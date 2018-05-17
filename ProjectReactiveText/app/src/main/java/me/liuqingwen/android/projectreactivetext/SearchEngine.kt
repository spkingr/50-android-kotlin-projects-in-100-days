package me.liuqingwen.android.projectreactivetext

import android.content.Context

/**
 * Created by Qingwen on 2018-5-1, project: ProjectReactiveText.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-1
 * @Package: me.liuqingwen.android.projectreactivetext in project: ProjectReactiveText
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class SearchEngine(private val context: Context)
{
    companion object
    {
        val SLEEP_TIME = 2000L
    }
    
    private val strings = this.context.resources.getStringArray(R.array.strings)
    
    fun search(text:String) = this.strings.filter { it.toLowerCase().contains(text.toLowerCase()) }.also {
        Thread.sleep(SearchEngine.SLEEP_TIME)
    }
}