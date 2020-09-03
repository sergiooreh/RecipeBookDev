package ua.co.myrecipes.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.repository.recipe.RecipeRepository
import ua.co.myrecipes.repository.recipe.RecipeRepositoryInt
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType

class RecipeViewModel @ViewModelInject constructor(
    val app: Application,
    private val recipeRepository: RecipeRepositoryInt
): AndroidViewModel(app) {

    private var _recipes: MutableLiveData<DataState<List<Recipe>>> = MutableLiveData()
    val recipes: LiveData<DataState<List<Recipe>>>
        get() = _recipes

    fun loadRecipes(recipeType: RecipeType){
        recipeRepository.loadRecipes(recipeType)
            .onEach { _recipes.value = it }
            .launchIn(viewModelScope)
    }

    fun insertRecipe(recipe: Recipe){
        viewModelScope.launch {
            recipeRepository.addRecipe(recipe)
        }
    }

}
