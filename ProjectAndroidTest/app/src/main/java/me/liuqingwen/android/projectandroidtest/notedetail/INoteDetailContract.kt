package me.liuqingwen.android.projectandroidtest.notedetail

import me.liuqingwen.android.projectandroidtest.data.Note

/**
 * Created by Qingwen on 2018-5-31, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-31
 * @Package: me.liuqingwen.android.projectandroidtest.notedetail in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

interface INoteDetailContract
{
    interface IView
    {
        fun showProgress()
        fun hideProgress()
        
        fun displayNote(note:Note)
        fun displayMissingNote()
    }
    
    interface IPresenter
    {
        fun getNote(id:Int)
        
        fun dispose()
    }
}