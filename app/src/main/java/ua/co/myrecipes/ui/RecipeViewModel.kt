package ua.co.myrecipes.ui

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ua.co.myrecipes.model.Ingredient
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.repository.RecipeRepository
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType

class RecipeViewModel @ViewModelInject constructor(
    val app: Application,
    private val recipeRepository: RecipeRepository
): AndroidViewModel(app) {

    fun insertRecipe(recipe: Recipe) = recipeRepository.addRecipe(recipe)

    fun loadRecipes(recipeType: RecipeType): LiveData<DataState<List<Recipe>>> = recipeRepository.loadRecipes(recipeType).asLiveData()


}
