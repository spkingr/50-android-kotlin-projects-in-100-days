package me.liuqingwen.android.projectandroidtest.addnote

import me.liuqingwen.android.projectandroidtest.data.Note

/**
 * Created by Qingwen on 2018-5-31, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-31
 * @Package: me.liuqingwen.android.projectandroidtest.addnote in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

interface IAddNoteContract
{
    interface IView
    {
        fun showProgress()
        fun hideProgress()
        
        fun navigateBackHome()
    }
    
    interface IPresenter
    {
        fun saveNote(note: Note)
        
        fun dispose()
    }
}