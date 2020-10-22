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
    val recipes: LiveData<DataState<List<Recipe>>>
        get() = _recipes

    private var _recipe: MutableLiveData<DataState<Recipe>> = MutableLiveData()
    val recipe: LiveData<DataState<Recipe>>
        get() = _recipe

    fun loadRecipes(recipeType: RecipeType){
        recipeRepository.loadRecipesByType(recipeType)
            .onEach { _recipes.value = it }
            .launchIn(viewModelScope)
    }

    fun loadRecipesCurrentUser(){
        recipeRepository.loadRecipesCurrentUser()
            .onEach { _recipes.value = it }
            .launchIn(viewModelScope)
    }

    fun loadLikedRecipes(){
        recipeRepository.loadMyLikedRecipes()
            .onEach { _recipes.value = it }
            .launchIn(viewModelScope)
    }

    fun loadRecipesUser(userName: String){
        recipeRepository.loadRecipesUser(userName)
            .onEach { _recipes.value = it }
            .launchIn(viewModelScope)
    }

    fun loadRecipe(recipe: Recipe){
       recipeRepository.loadRecipe(recipe)
           .onEach { _recipe.value = it }
           .launchIn(viewModelScope)
    }

    fun insertRecipe(recipe: Recipe){
        viewModelScope.launch {
            recipeRepository.addRecipe(recipe)
        }
    }

    fun addLikedRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeRepository.addLikedRecipe(recipe)
    }

    fun removeLikedRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeRepository.removeLikedRecipe(recipe)
    }

    fun isLikedRecipeAsync(recipe: Recipe) = viewModelScope.async {
        recipeRepository.isLikedRecipe(recipe)
    }



}
