package me.liuqingwen.android.projectandroidtest

import io.mockk.*
import me.liuqingwen.android.projectandroidtest.addnote.IAddNoteContract
import me.liuqingwen.android.projectandroidtest.data.INotesRepository
import me.liuqingwen.android.projectandroidtest.data.Note
import me.liuqingwen.android.projectandroidtest.notedetail.INoteDetailContract
import me.liuqingwen.android.projectandroidtest.notedetail.NoteDetailPresenter
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test

/**
 * Created by Qingwen on 2018-7-25, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-7-25
 * @Package: me.liuqingwen.android.projectandroidtest in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class NoteDetailPresenterTest
{
    companion object
    {
        private val FAKE_DATA = Note(1, "Title 1", "Content 1", "")
        
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }
    
    private val notesRepository = spyk<INotesRepository>()
    private val noteDetailView = spyk<INoteDetailContract.IView>()
    private val getNoteCallback = slot<(Note?) -> Unit>()
    
    private lateinit var noteDetailPresenter : NoteDetailPresenter
    
    @Before
    fun setUpPresenter()
    {
        this.noteDetailPresenter = NoteDetailPresenter(this.notesRepository, this.noteDetailView)
    }
    
    @Test
    fun get_note_test()
    {
        every {
            notesRepository.getNote(any(), capture(getNoteCallback))
        } answers {
            getNoteCallback.invoke(FAKE_DATA)
        }
        
        this.noteDetailPresenter.getNote(0)
        
        verifySequence {
            noteDetailView.showProgress()
            noteDetailView.hideProgress()
            noteDetailView.displayNote(FAKE_DATA)
        }
        
        verify(exactly = 0){
            noteDetailView.displayMissingNote()
        }
    }
}