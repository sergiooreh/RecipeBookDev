package ua.co.myrecipes.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ua.co.myrecipes.R

@ExperimentalCoroutinesApi
@HiltAndroidTest
class MainActivityTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup(){
        hiltRule.inject()
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun testActivity(){
        onView(withId(R.id.recipeTypes_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun openAddNewRecipeFragment(){
        onView(withId(R.id.newRecipeFragment)).perform(click())
        onView(withId(R.id.new_recipe_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun openProfileFragment(){
        onView(withId(R.id.profileFragment)).perform(click())
    }
}