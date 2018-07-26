package me.liuqingwen.android.projectandroidtest.util

import android.support.test.espresso.IdlingResource
import android.support.test.espresso.IdlingResource.ResourceCallback
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Qingwen on 2018-6-1, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-6-1
 * @Package: me.liuqingwen.android.projectandroidtest.util in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

object EspressoIdlingResource1
{
    val idlingResource = SimpleCountingIdlingResource("GLOBAL_NAME")
    fun increment() = this.idlingResource.increment()
    fun decrement() = this.idlingResource.decrement()
}

class SimpleCountingIdlingResource(private val resourceName: String) : IdlingResource
{
    @Volatile
    private var resourceCallback: ResourceCallback? = null
    private val counter = AtomicInteger(0)
    
    override fun getName() = this.resourceName
    
    override fun isIdleNow() = this.counter.get() == 0
    
    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?)
    {
        this.resourceCallback = callback
    }
    
    fun increment() = this.counter.getAndIncrement()
    
    fun decrement()
    {
        val value = this.counter.decrementAndGet()
        if (value == 0)
        {
            this.resourceCallback?.onTransitionToIdle()
        }else if (value < 0)
        {
            throw IllegalArgumentException("Counter has been corrupted!")
        }
    }
}