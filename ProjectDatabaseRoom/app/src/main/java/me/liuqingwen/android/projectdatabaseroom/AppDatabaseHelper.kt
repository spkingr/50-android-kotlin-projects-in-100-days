package me.liuqingwen.android.projectdatabaseroom

import android.arch.persistence.room.Room
import android.content.Context

/**
 * Created by Qingwen on 2017-9-4.
 */

class AppDatabaseHelper private constructor(context: Context)
{
    companion object
    {
        @Volatile
        private var INSTANCE:AppDatabaseHelper? = null
        fun getInstance(context: Context) = if(INSTANCE == null) { synchronized(AppDatabaseHelper::class){ INSTANCE = AppDatabaseHelper(context); INSTANCE!!; } } else INSTANCE!!
    }
    
    private val appDatabase by lazy(LazyThreadSafetyMode.NONE) { Room.databaseBuilder(context, AppDatabase::class.java, "mydatabase.db").allowMainThreadQueries().build()!! }
    private val postDao by lazy(LazyThreadSafetyMode.NONE) { this.appDatabase.postDao() }
    
    fun getAll() = this.postDao.getAllPost()
    fun getPostById(id:Long) = this.postDao.getPostById(id)
    fun newPost(post: Post) = this.postDao.addPost(post)
    fun newPosts(vararg post:Post) = this.postDao.addPosts(*post)
    fun delete(post: Post) = this.postDao.deletePost(post)
}