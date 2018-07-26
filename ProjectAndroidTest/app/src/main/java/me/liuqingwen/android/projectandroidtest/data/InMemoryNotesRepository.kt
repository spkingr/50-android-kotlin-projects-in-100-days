package me.liuqingwen.android.projectandroidtest.data

import android.support.annotation.VisibleForTesting

/**
 * Created by Qingwen on 2018-5-31, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-31
 * @Package: me.liuqingwen.android.projectandroidtest.data in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class InMemoryNotesRepository(private val serviceApi: INotesServiceApi) : INotesRepository
{
    @VisibleForTesting
    private val cachedNotes by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<Note>() }
    
    override fun getNotes(callback: (List<Note>) -> Unit)
    {
        if (this.cachedNotes.isEmpty())
        {
            this.serviceApi.getAllNotes{
                this.cachedNotes.addAll(it)
                callback(this.cachedNotes)
            }
        }
        else
        {
            callback(this.cachedNotes)
        }
    }
    
    override fun getNote(noteId: Int, callback: (Note?) -> Unit)
    {
        this.serviceApi.getNote(noteId, callback)
    }
    
    override fun saveNote(note: Note, callback: (Boolean) -> Unit)
    {
        this.serviceApi.saveNote(note, callback)
        this.refreshData()
    }
    
    override fun refreshData()
    {
        this.cachedNotes.clear()
    }
    
}