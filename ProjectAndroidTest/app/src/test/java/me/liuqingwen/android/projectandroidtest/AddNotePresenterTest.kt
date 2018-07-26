package me.liuqingwen.android.projectandroidtest

import io.mockk.*
import me.liuqingwen.android.projectandroidtest.addnote.AddNotePresenter
import me.liuqingwen.android.projectandroidtest.addnote.IAddNoteContract
import me.liuqingwen.android.projectandroidtest.data.INotesRepository
import me.liuqingwen.android.projectandroidtest.data.Note
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

class AddNotePresenterTest
{
    companion object
    {
        private val FAKE_DATA = Note(1, "Title 1", "Content 1", "")
    
        @ClassRule @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }
    
    private val notesRepository = spyk<INotesRepository>()
    private val addNotesView = spyk<IAddNoteContract.IView>()
    
    private val addNoteCallback = slot<(Boolean) -> Unit>()
    private lateinit var addNotePresenter: AddNotePresenter
    
    @Before
    fun setUpPresenter()
    {
        this.addNotePresenter = AddNotePresenter(this.notesRepository, this.addNotesView)
    }
    
    @Test
    fun add_note_test()
    {
        every {
            notesRepository.saveNote(any(), capture(addNoteCallback))
        }answers {
            addNoteCallback.invoke(true)
            notesRepository.refreshData()
        }
        
        this.addNotePresenter.saveNote(FAKE_DATA)
        
        verifySequence {
            addNotesView.showProgress()
            addNotesView.hideProgress()
            addNotesView.navigateBackHome()
        }
        verify {
            notesRepository.refreshData()
        }
    }
}