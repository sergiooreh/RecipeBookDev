package ua.co.myrecipes.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.repository.recipe.RecipeRepositoryInt
import ua.co.myrecipes.util.Event
import ua.co.myrecipes.util.RecipeType
import ua.co.myrecipes.util.Resource
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepositoryInt,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    private var _recipes: MutableLiveData<Event<Resource<List<Recipe>>>> = MutableLiveData()
    val recipes: LiveData<Event<Resource<List<Recipe>>>> = _recipes

    private var _recipe: MutableLiveData<Event<Resource<Recipe>>> = MutableLiveData()
    val recipe: LiveData<Event<Resource<Recipe>>> = _recipe

    private val _likePostStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val likePostStatus: LiveData<Event<Resource<Boolean>>> = _likePostStatus

    fun loadRecipesByType(recipeType: RecipeType) = viewModelScope.launch(dispatcher) {
        _recipes.postValue(Event(Resource.Loading()))
        val result = recipeRepository.getRecipesByType(recipeType)
        _recipes.postValue(Event(result))
    }

    fun loadCurrentUserRecipes() = viewModelScope.launch(dispatcher) {
        _recipes.postValue(Event(Resource.Loading()))
        val result = recipeRepository.getCurrentUserRecipes()
        _recipes.postValue(Event(result))
    }

    fun loadMyLikedRecipes() = viewModelScope.launch(dispatcher) {
        _recipes.postValue(Event(Resource.Loading()))
        val result = recipeRepository.getMyLikedRecipes()
        _recipes.postValue(Event(result))
    }

    fun loadRecipesByUserName(userName: String) = viewModelScope.launch(dispatcher) {
        _recipes.postValue(Event(Resource.Loading()))
        val result = recipeRepository.getRecipesByUserName(userName)
        _recipes.postValue(Event(result))
    }

    fun loadRecipe(recipe: Recipe) = viewModelScope.launch(dispatcher) {
        _recipe.postValue(Event(Resource.Loading()))
        val result = recipeRepository.getRecipe(recipe)
        _recipe.postValue(Event(result))
    }

    fun insertRecipe(recipe: Recipe) = viewModelScope.launch(dispatcher) {
        recipeRepository.insertRecipe(recipe)
    }

    fun toggleLikeForRecipe(recipe: Recipe) {
        _likePostStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = recipeRepository.toggleLikeForRecipe(recipe)
            _likePostStatus.postValue(Event(result))
        }
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch(dispatcher) {
        recipeRepository.deleteRecipe(recipe)
    }
}
