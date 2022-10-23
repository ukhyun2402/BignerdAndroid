package com.wookhyun.criminalintent

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrimeDetailFragmentTest{

    lateinit var fragmentScenario: FragmentScenario<CrimeDetailFragment>

    @Before
    fun setUp() {
        fragmentScenario = launchFragmentInContainer<CrimeDetailFragment>(
            initialState = Lifecycle.State.RESUMED
        )
    }

    @After
    fun tearDown() {
        fragmentScenario.close()
    }

    @Test
    fun test(){
        onView(withId(R.id.crime_solved)).perform(click())
        fragmentScenario.onFragment{fragment ->
            assertEquals(true, fragment.crime.isSolved)
        }

    }

    @Test
    fun testEditText(){
        onView(withId(R.id.crime_title)).perform(typeText("Hello"))
        fragmentScenario.onFragment{fragment ->
            assertEquals("Hello", fragment.crime.title)
        }
    }
}