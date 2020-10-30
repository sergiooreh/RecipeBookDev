package ua.co.myrecipes.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.repository.recipe.RecipeRepositoryInt
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType

class RecipeViewModel @ViewModelInject constructor(
    val app: Application,
    private val recipeRepository: RecipeRepositoryInt
): AndroidViewModel(app) {

    private var _recipes: MutableLiveData<DataState<List<Recipe>>> = MutableLiveData()
    val recipes: LiveData<DataState<List<Recipe>>> = _recipes

    private var _recipe: MutableLiveData<DataState<Recipe>> = MutableLiveData()
    val recipe: LiveData<DataState<Recipe>> = _recipe

    fun loadRecipesByType(recipeType: RecipeType){
        recipeRepository.getRecipesByType(recipeType)
            .onEach { _recipes.value = it }
            .launchIn(viewModelScope)
    }

    fun loadCurrentUserRecipes(){
        recipeRepository.getCurrentUserRecipes()
            .onEach { _recipes.value = it }
            .launchIn(viewModelScope)
    }

    fun loadMyLikedRecipes(){
        recipeRepository.getMyLikedRecipes()
            .onEach { _recipes.value = it }
            .launchIn(viewModelScope)
    }

    fun loadRecipesByUserName(userName: String){
        recipeRepository.getRecipesByUserName(userName)
            .onEach { _recipes.value = it }
            .launchIn(viewModelScope)
    }


    //TODO : flow?
    fun loadRecipe(recipe: Recipe){
       recipeRepository.getRecipe(recipe)
           .onEach { _recipe.value = it }
           .launchIn(viewModelScope)
    }

    fun insertRecipe(recipe: Recipe){
        viewModelScope.launch {
            recipeRepository.insertRecipe(recipe)
        }
    }

    fun addLikedRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeRepository.addLikedRecipe(recipe)
    }

    fun removeLikedRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeRepository.removeLikedRecipe(recipe)
    }

    /*TODO: waiting Philip answer*/
    fun isLikedRecipeAsync(recipe: Recipe) = viewModelScope.async {
        recipeRepository.isLikedRecipe(recipe)
    }



}
