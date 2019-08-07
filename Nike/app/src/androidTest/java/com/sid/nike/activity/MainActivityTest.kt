package com.sid.nike.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.sid.nike.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @Rule @JvmField var activityRule = ActivityTestRule(MainActivity::class.java)
    companion object {
        val INVALID_SEARCH_TERM = "IAmAndroidDeveloper"
        val VALID_SEARCH_TERM = "Android"
    }

    /**
     * Test Case to check if definitions for valid term are loaded.
     */
    @Test fun searchTerm(){
        onView(withId(R.id.menu_main_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText(VALID_SEARCH_TERM)).perform(pressImeActionButton())
        Thread.sleep(3000)
        onView(withText(VALID_SEARCH_TERM.toUpperCase())).check(matches(isDisplayed()))
    }

    /**
     * Test case to check if snack bar is shown when invalid term is entered by the user.
     */
    @Test fun searchInvalidTerm(){
        onView(withId(R.id.menu_main_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText(INVALID_SEARCH_TERM)).perform(pressImeActionButton())
        Thread.sleep(2000)
        var snackBarMessage = String.format(activityRule.activity.getString(R.string.cannot_find_term),INVALID_SEARCH_TERM)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(snackBarMessage)))
    }
}
