package me.liuqingwen.android.projectandroidtest.noteslist

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.liuqingwen.android.projectandroidtest.data.INotesRepository
import me.liuqingwen.android.projectandroidtest.data.Note
import org.jetbrains.anko.AnkoLogger

/**
 * Created by Qingwen on 2018-5-31, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-5-31
 * @Package: me.liuqingwen.android.projectandroidtest.noteslist in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class NotesListPresenter(private val notesRepository:INotesRepository,
                         private val view:INotesListContract.IView) : INotesListContract.IPresenter, AnkoLogger
{
    private var disposable : Disposable? = null
    
    override fun loadData()
    {
        this.disposable?.dispose()
        
        this.disposable = Observable.create<List<Note>> {emitter->
            this.notesRepository.getNotes {
                emitter.onNext(it)
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    //EspressoIdlingResource.increment()
                    this.view.showProgress()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    //EspressoIdlingResource.decrement()
                    this.view.hideProgress()
                    this.view.displayNotes(it)
                }
    }
    
    override fun addNote()
    {
        this.view.navigateToAddNote()
    }
    
    override fun showNoteDetail(noteId:Int)
    {
        this.view.navigateToNoteDetail(noteId)
    }
    
    override fun dispose()
    {
        if (this.disposable?.isDisposed != true)
        {
            this.disposable?.dispose()
        }
    }
}