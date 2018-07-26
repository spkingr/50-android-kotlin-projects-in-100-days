package me.liuqingwen.android.projectandroidtest.notedetail

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.liuqingwen.android.projectandroidtest.data.INotesRepository
import me.liuqingwen.android.projectandroidtest.data.Note

/**
 * Created by Qingwen on 2018-6-9, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-6-9
 * @Package: me.liuqingwen.android.projectandroidtest.notedetail in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class NoteDetailPresenter(private val notesRepository: INotesRepository,
                          private val view: INoteDetailContract.IView): INoteDetailContract.IPresenter
{
    
    private class Wrapper<T : Any?>(val value: T)
    
    private var disposable : Disposable? = null
    
    override fun getNote(id: Int)
    {
        this.disposable = Observable.create<Wrapper<Note?>>{ emitter->
            this.notesRepository.getNote(id) {
                emitter.onNext(Wrapper(it))
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { this.view.showProgress() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    this.view.hideProgress()
                    if (it.value != null) this.view.displayNote(it.value) else this.view.displayMissingNote()
                }
    }
    
    override fun dispose()
    {
        if (this.disposable?.isDisposed != true)
        {
            this.disposable?.dispose()
        }
    }
}