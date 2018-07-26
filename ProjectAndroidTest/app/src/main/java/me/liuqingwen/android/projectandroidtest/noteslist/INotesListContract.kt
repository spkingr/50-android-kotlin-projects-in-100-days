package me.liuqingwen.android.projectandroidtest.noteslist

import me.liuqingwen.android.projectandroidtest.data.Note

/**
 * Created by Qingwen on 2018-5-30, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-30
 * @Package: me.liuqingwen.android.projectandroidtest.noteslist in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

interface INotesListContract
{
    interface IView
    {
        fun showProgress()
        fun hideProgress()
        
        fun displayNotes(notes:List<Note>)
        
        fun navigateToAddNote()
        fun navigateToNoteDetail(noteId:Int)
    }
    
    interface IPresenter
    {
        fun loadData()
        
        fun addNote()
        fun showNoteDetail(noteId:Int)
        
        fun dispose()
    }
}