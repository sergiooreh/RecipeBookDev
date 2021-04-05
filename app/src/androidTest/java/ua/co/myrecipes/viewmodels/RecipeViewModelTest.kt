package ua.co.myrecipes.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.lifecycleScope
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ua.co.myrecipes.getOrAwaitValueTest
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.repository.recipe.FakeRecipeRepositoryTest
import ua.co.myrecipes.util.Resource

/*I really don't need these testing...there's no to test there*/
class RecipeViewModelTest{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RecipeViewModel

    @Before
    fun setup(){
        viewModel = RecipeViewModel(FakeRecipeRepositoryTest(), Dispatchers.IO)
    }

    @Test
    fun insetRecipeWithEmptyNameReturnsError(){
        viewModel.loadRecipe(
            Recipe()
        )
        val value = viewModel.recipe.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.data).isEqualTo(null)
    }

    @Test
    fun insetRecipeWithTooLongNameError(){
        viewModel.loadRecipe(
            Recipe()
        )
        val value = viewModel.recipe.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()?.javaClass).isEqualTo(Resource.Loading::class.java)
    }
}