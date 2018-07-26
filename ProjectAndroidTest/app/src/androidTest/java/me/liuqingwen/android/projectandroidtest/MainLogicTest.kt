package me.liuqingwen.android.projectandroidtest

import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import me.liuqingwen.android.projectandroidtest.addnote.AddNoteUI
import me.liuqingwen.android.projectandroidtest.notedetail.NoteDetailUI
import me.liuqingwen.android.projectandroidtest.noteslist.NoteViewHolder
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Created by Qingwen on 2018-7-25, project: ProjectAndroidTest.
 *
 * @Author: Qingwen
 * @DateTime: 2018-7-25
 * @Package: me.liuqingwen.android.projectandroidtest in project: ProjectAndroidTest
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainLogicTest
{
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)
    
    private lateinit var activity: MainActivity
    
    @Before
    fun setUpActivity()
    {
        this.activity = activityTestRule.activity
    }
    
    @After
    fun tearDown()
    {
    }
    
    @Test
    fun click_add_note_test()
    {
        onView(withId(MainUI.ID_BUTTON_FLOATING)).perform(click())
        onView(withId(AddNoteUI.ID_TEXT_TITLE)).check(matches(isDisplayed()))
        onView(withId(AddNoteUI.ID_TEXT_CONTENT)).check(matches(isDisplayed()))
        onView(withId(AddNoteUI.ID_IMAGE_CONTENT)).check(matches(isDisplayed()))
    }
    
    @Test
    fun add_note_to_main_test()
    {
        val title = "Title Test"
        val content = "Content Test"
        
        onView(withId(MainUI.ID_BUTTON_FLOATING)).perform(click())
        
        onView(withId(AddNoteUI.ID_TEXT_TITLE)).perform(typeText(title), closeSoftKeyboard())
        onView(withId(AddNoteUI.ID_TEXT_CONTENT)).perform(typeText(content), closeSoftKeyboard())
        //onView(withId(AddNoteUI.ID_IMAGE_CONTENT)).perform(click())
        //Espresso.pressBack()
        
        onView(withId(MainUI.ID_BUTTON_FLOATING)).perform(click())
        
        //idle
        BaristaSleepInteractions.sleep(5000L, TimeUnit.MILLISECONDS)
        onView(withId(R.id.recyclerNotesList)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerNotesList)).perform(RecyclerViewActions.actionOnItem<NoteViewHolder>(allOf(hasDescendant(withText(title)), hasDescendant(withText(content))), click()))
    
        //idle
        BaristaSleepInteractions.sleep(3000L, TimeUnit.MILLISECONDS)
        onView(withId(NoteDetailUI.ID_IMAGE_CONTENT)).check(matches(isDisplayed()))
        onView(withId(NoteDetailUI.ID_LABEL_TITLE)).check(matches(allOf(withText(title), isDisplayed())))
        onView(withId(NoteDetailUI.ID_LABEL_CONTENT)).check(matches(allOf(withText(content), isDisplayed())))
        
        Espresso.pressBack()
    }
}