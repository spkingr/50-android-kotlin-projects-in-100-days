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

object NotesRepositories
{
    private var repository : INotesRepository? = null
    
    @Synchronized
    fun getRepository(serviceApi: INotesServiceApi) : INotesRepository
    {
        if (this.repository == null)
        {
            this.repository = InMemoryNotesRepository(serviceApi)
        }
        return this.repository!!
    }
}