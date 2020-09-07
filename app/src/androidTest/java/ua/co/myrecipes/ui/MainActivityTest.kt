package ua.co.myrecipes.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ua.co.myrecipes.R

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest{
    private var isFirstAppOpen = false

    @Before
    fun setup(){
        isFirstAppOpen = true
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun testActivity(){
        if (isFirstAppOpen){
            onView(withId(R.id.welcomeReg_layout)).check(matches(isDisplayed()))
        } else{
            onView(withId(R.id.recipeTypes_layout)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun openAddNewRecipeFragment(){
        onView(withId(R.id.newRecipeFragment)).perform(click())
        onView(withId(R.id.new_recipe_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun openProfileFragment(){
        onView(withId(R.id.profileFragment)).perform(click())
        onView(withId(R.id.profile_layout)).check(matches(isDisplayed()))
    }
}