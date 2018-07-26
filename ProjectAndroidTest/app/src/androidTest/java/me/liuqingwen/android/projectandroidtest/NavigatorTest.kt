package me.liuqingwen.android.projectandroidtest

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.DrawerMatchers.isClosed
import android.support.test.espresso.contrib.DrawerMatchers.isOpen
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.Gravity

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class NavigatorTest
{
    @Rule @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)
    
    private lateinit var activity: MainActivity
    
    @Before
    fun setUpActivity()
    {
        this.activity = activityTestRule.activity
    }
    
    @Test
    fun use_app_context()
    {
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("me.liuqingwen.android.projectandroidtest", appContext.packageName)
    }
    
    @Test
    fun click_on_android_toolbar_home_button()
    {
        onView(withId(MainUI.ID_LAYOUT_DRAWER)).check(matches(isClosed(Gravity.LEFT)))
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withId(MainUI.ID_LAYOUT_DRAWER)).check(matches(isOpen(Gravity.LEFT)))
    }
}
