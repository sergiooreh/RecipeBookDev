package ua.co.myrecipes.ui.fragments.newRecipe

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.Instrumentation.ActivityResult
import android.content.ContentResolver
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import ua.co.myrecipes.R
import ua.co.myrecipes.launchFragmentInHiltContainer

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class NewRecipeFragmentTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup(){
        hiltRule.inject()
    }

    @Test
    fun clickRecipeMenuItem_navigateHomeFragment(){
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<NewRecipeFragment> {
            Navigation.setViewNavController(requireView(),navController)            //set mock NavController to fragment
        }
        onView(withId(R.id.recipe_name_et)).perform(typeText("Potatoes"))


    }

    @Test
    fun  test_validateIntentSentToPickPackage() {
        // GIVEN
        val expectedIntent: Matcher<Intent> = allOf(
            hasAction(Intent.ACTION_PICK),
            hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        )

        val activityResult = createGalleryPickActivityResultStub()
        intending(expectedIntent).respondWith(activityResult)

        // Execute and Verify
        onView(withId(R.id.add_recipe_img)).perform(click())
        intended(expectedIntent)
    }

    private fun createGalleryPickActivityResultStub(): ActivityResult {
        val resources: Resources = InstrumentationRegistry.getInstrumentation().context.resources
        val imageUri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.ic_launcher_background))
            .appendPath(resources.getResourceTypeName(R.drawable.ic_launcher_background))
            .appendPath(resources.getResourceEntryName(R.drawable.ic_launcher_background))
            .build()
        val resultIntent = Intent()
        resultIntent.data = imageUri
        return ActivityResult(RESULT_OK, resultIntent)
    }
}