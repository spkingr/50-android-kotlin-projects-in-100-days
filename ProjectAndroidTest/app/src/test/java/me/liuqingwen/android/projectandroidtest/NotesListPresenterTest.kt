package me.liuqingwen.android.projectandroidtest

import io.mockk.*
import io.reactivex.schedulers.TestScheduler
import me.liuqingwen.android.projectandroidtest.data.INotesRepository
import me.liuqingwen.android.projectandroidtest.data.Note
import me.liuqingwen.android.projectandroidtest.noteslist.INotesListContract
import me.liuqingwen.android.projectandroidtest.noteslist.NotesListPresenter
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test

/**
 * Created by Qingwen on 2018-5-31, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-31
 * @Package: me.liuqingwen.android.projectandroidtest in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class NotesListPresenterTest
{
    companion object
    {
        private val FAKE_DATA = listOf(
                Note(1, "Title 1", "Content 1", ""),
                Note(2, "Title 2", "Content 2", ""),
                Note(3, "Title 3", "Content 3", "")
                                      )
        @ClassRule @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }
    
    private val notesRepository = spyk<INotesRepository>()
    private val notesListView = spyk<INotesListContract.IView>()
    
    private val loadCallback = slot<(List<Note>) -> Unit>()
    private lateinit var notesListPresenter:INotesListContract.IPresenter
    
    @Before
    fun setUpPresenter()
    {
        this.notesListPresenter = NotesListPresenter(this.notesRepository, this.notesListView)
    }
    
    @Test
    fun load_notes_from_repository_and_display()
    {
        every {
            notesRepository.getNotes(capture(loadCallback))
        } answers {
            loadCallback.invoke(FAKE_DATA)
        }
        
        this.notesListPresenter.loadData()
        
        verifySequence {
            notesListView.showProgress()
            notesListView.hideProgress()
            notesListView.displayNotes(FAKE_DATA)
        }
    }
    
    @Test
    fun click_note_to_show_detail_ui()
    {
        val note = NotesListPresenterTest.FAKE_DATA.first()
        every { notesListView.navigateToNoteDetail(any()) } just Runs
        //every { notesRepository.getNote(note.id, capture(noteCallback)) } answers { noteCallback.invoke(note) }
        this.notesListPresenter.showNoteDetail(note.id)
        verify {
            notesListView.navigateToNoteDetail(note.id)
            //notesRepository.getNote(note.id, any())
        }
    }
    
    @Test
    fun click_add_button_to_add_note_ui()
    {
        every { notesListView.navigateToAddNote() } just Runs
        this.notesListPresenter.addNote()
        verify {
            notesListView.navigateToAddNote()
        }
    }
}