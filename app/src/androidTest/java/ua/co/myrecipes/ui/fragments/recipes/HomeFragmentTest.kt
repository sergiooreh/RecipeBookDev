package ua.co.myrecipes.ui.fragments.recipes

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import ua.co.myrecipes.R
import ua.co.myrecipes.launchFragmentInHiltContainer
import ua.co.myrecipes.ui.fragments.newRecipe.NewRecipeFragment

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class HomeFragmentTest{
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup(){
        hiltRule.inject()
    }

    @Test
    fun clickRecipeMenuItem_navigateHomeFragment(){
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<HomeFragment> {
            Navigation.setViewNavController(requireView(),navController)            //set mock NavController to fragment
        }
        Espresso.onView(withId(R.id.recipeTypes))
            .perform(click())
        Espresso.onView(withId(R.id.to_ingredients_fab)).perform(click())
        verify(navController).navigate(
            R.id.homeFragment
        )
    }
}