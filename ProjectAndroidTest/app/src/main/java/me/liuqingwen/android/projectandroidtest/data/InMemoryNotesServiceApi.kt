package me.liuqingwen.android.projectandroidtest.data

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import org.jetbrains.anko.doAsync

/**
 * Created by Qingwen on 2018-5-31, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-31
 * @Package: me.liuqingwen.android.projectandroidtest.data in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class InMemoryNotesServiceApi:INotesServiceApi
{
    companion object
    {
        private const val SERVICE_LOADING_DELAY = 2000L
    }
    
    override fun getAllNotes(callback: (List<Note>) -> Unit)
    {
        try
        {
            Thread.sleep(InMemoryNotesServiceApi.SERVICE_LOADING_DELAY)
        }catch (e:InterruptedException){
        }
        
        val data = NotesServiceApiEndPoint.loadPersistedData()
        callback(data)
    }
    
    override fun getNote(noteId: Int, callback: (Note?) -> Unit)
    {
        try
        {
            Thread.sleep(InMemoryNotesServiceApi.SERVICE_LOADING_DELAY)
        }catch (e:InterruptedException){
        }
        
        val note = NotesServiceApiEndPoint.loadPersistedData().firstOrNull { it.id == noteId }
        callback(note)
    }
    
    override fun saveNote(note: Note, callback: (Boolean) -> Unit)
    {
        try
        {
            Thread.sleep(InMemoryNotesServiceApi.SERVICE_LOADING_DELAY)
        }catch (e:InterruptedException){
        }
        
        val data = NotesServiceApiEndPoint.loadPersistedData()
        note.id = data.map { it.id + 1 }.max() ?: 0
        callback(data.add(note))
    }
}