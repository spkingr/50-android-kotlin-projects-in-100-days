package me.liuqingwen.android.projectandroidtest

import io.mockk.*
import me.liuqingwen.android.projectandroidtest.data.InMemoryNotesRepository
import me.liuqingwen.android.projectandroidtest.data.InMemoryNotesServiceApi
import me.liuqingwen.android.projectandroidtest.data.Note
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Created by Qingwen on 2018-6-18, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-6-18
 * @Package: me.liuqingwen.android.projectandroidtest in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class InMemoryNotesRepositoryTest
{
    private lateinit var repository : InMemoryNotesRepository
    
    private val service = mockk<InMemoryNotesServiceApi>()
    
    private val loadCallback = slot<(List<Note>) -> Unit>()
    private val noteCallback = slot<(Note?) -> Unit>()
    private val saveCallback = slot<(Boolean) -> Unit>()
    
    @Before
    fun setupRepository()
    {
        this.repository = InMemoryNotesRepository(this.service)
    }
    
    @Test
    fun test_get_all_notes()
    {
        val list = listOf(Note(0, "Title1", "Content1", ""), Note(0, "Title2", "Content2", ""), Note(0, "Title3", "Content3", ""))
        every { service.getAllNotes(capture(loadCallback)) } answers { loadCallback.invoke(list) }
        this.repository.getNotes{
            assert(it.size == list.size)
            assert(it.first() == list.first())
        }
        assert(loadCallback.isCaptured)
        verify {
            //repository.getNotes(any()) //real object should not in verify test?
            service.getAllNotes(any())
        }
    }
    
    @Test
    fun test_get_note()
    {
        val note = Note(0, "Title", "Content", "")
        every { service.getNote(any(), capture(noteCallback)) } answers { noteCallback.invoke(note) }
        this.repository.getNote(1){
            assert(it != null)
            assert(it!!.title == note.title)
        }
        assert(noteCallback.isCaptured)
        verify {
            //repository.getNote(any(), any())
            service.getNote(any(), any())
        }
    }
    
    @Test
    fun test_save_note()
    {
        val note = Note(0, "Title", "Content", "")
        every { service.saveNote(note, capture(saveCallback)) } answers { saveCallback.invoke(true) }
        this.repository.saveNote(note){
            assert(it)
        }
        assert(saveCallback.isCaptured)
        verify {
            //repository.saveNote(any(), any())
            service.saveNote(any(), any())
        }
    }
    
    @Test
    @Ignore("How to test private fields?")
    fun test_cache()
    {
        this.repository.refreshData()
        verify {
            //repository.refreshData()
            repository getProperty "cachedNotes"
        }
    }
}