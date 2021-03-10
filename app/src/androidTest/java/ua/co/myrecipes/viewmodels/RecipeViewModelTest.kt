package ua.co.myrecipes.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ua.co.myrecipes.getOrAwaitValueTest
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.repository.recipe.FakeRecipeRepositoryTest
import ua.co.myrecipes.util.Resource

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

        assertThat(value.getContentIfNotHandled()).isEqualTo(Resource.Success::class)
    }

    @Test
    fun insetRecipeWithTooLongNameError(){
        viewModel.loadRecipe(
            Recipe()
        )
        val value = viewModel.recipe.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()).isEqualTo(Resource.Success::class)
    }
}