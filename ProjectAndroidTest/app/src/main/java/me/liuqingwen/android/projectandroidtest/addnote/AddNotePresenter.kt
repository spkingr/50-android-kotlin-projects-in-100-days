package me.liuqingwen.android.projectandroidtest.addnote

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
 * @Package: me.liuqingwen.android.projectandroidtest.addnote in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

class AddNotePresenter(private val notesRepository: INotesRepository,
                       private var view:IAddNoteContract.IView?) : IAddNoteContract.IPresenter
{
    private var disposable : Disposable? = null
    
    override fun saveNote(note: Note)
    {
        this.disposable = Observable.create<Boolean> {emitter->
            this.notesRepository.saveNote(note){
                emitter.onNext(it)
            }
        }
                //.timeout(3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    this.view?.showProgress()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    this.view?.hideProgress()
                    this.view?.navigateBackHome()
                }
    }
    
    override fun dispose()
    {
        if (this.disposable?.isDisposed != true)
        {
            this.disposable?.dispose()
        }
        this.view = null
    }
}