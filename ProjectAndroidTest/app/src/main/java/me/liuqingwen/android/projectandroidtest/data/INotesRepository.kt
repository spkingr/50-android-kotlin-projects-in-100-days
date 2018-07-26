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

interface INotesRepository
{
    fun getNotes(callback : ( List<Note> ) -> Unit)
    fun getNote(noteId : Int, callback: ( Note? ) -> Unit)
    fun saveNote(note : Note, callback: (Boolean) -> Unit)
    fun refreshData()
}