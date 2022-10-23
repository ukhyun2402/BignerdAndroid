package com.wookhyun.geoquiz

import android.app.Activity
import android.app.Instrumentation
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers.not
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var scenario: ActivityScenario<MainActivity>


    @Before
    fun setUp() {
        scenario = launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun showsFirstQuestionOnLaunch() {
        onView(withId(R.id.quiz_text))
            .check(matches(withText(R.string.question_australia)))
    }

    @Test
    fun showsSecondQuestionAfterNextPress() {
        onView(withId(R.id.btn_next)).perform(click())
        scenario.recreate()
        onView(withId(R.id.quiz_text))
            .check(matches(withText(R.string.question_oceans)))
    }

    @Test
    fun activeCheatActivity(){
        onView(withId(R.id.cheat_button)).perform(click())
        onView(withId(R.id.warning_text)).check(matches(withText(R.string.warning_text)))
    }

//    @Test
//    fun checkCheater(){
//
//        onView(withId(R.id.cheat_button)).perform(click())
//        onView(withId(R.id.show_answer_buttons)).perform(click())
//        pressBack()
//        onView(withId(R.id.btn_true)).perform(click())
//        onView(withText(R.string.judgement_toast))
//            .inRoot(withDecorView(not(MainA?tivity().window.decorView))).check(matches(isDisplayed()))
//    }
}