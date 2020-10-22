package ua.co.myrecipes.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Rule
import ua.co.myrecipes.repository.recipe.FakeRecipeRepositoryTest

class RecipeViewModelTest{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RecipeViewModel

    @Before
    fun setup(){
        viewModel = RecipeViewModel(ApplicationProvider.getApplicationContext(), FakeRecipeRepositoryTest())
    }
}