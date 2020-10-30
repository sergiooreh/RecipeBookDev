package ua.co.myrecipes.repository.recipe

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType

class FakeRecipeRepositoryTest: RecipeRepositoryInt{
    private val recipeList = mutableListOf<Recipe>()
    private var shouldReturnError = false

    override fun getRecipesByType(recipeType: RecipeType): Flow<DataState<List<Recipe>>>  = flow {
        if (shouldReturnError){
            emit(DataState.Error(Exception("Error while loading recipes")))
        } else{
            emit(DataState.Success(recipeList))
        }
    }

    override fun getCurrentUserRecipes(): Flow<DataState<List<Recipe>>> {
        TODO("Not yet implemented")
    }

    override fun getMyLikedRecipes(): Flow<DataState<List<Recipe>>> {
        TODO("Not yet implemented")
    }

    override suspend fun addLikedRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun removeLikedRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override fun getRecipe(recipe: Recipe): Flow<DataState<Recipe>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        recipeList.add(recipe)
    }

    override fun getRecipesByUserName(userName: String): Flow<DataState<List<Recipe>>> {
        TODO("Not yet implemented")
    }

    override suspend fun isLikedRecipe(recipe: Recipe): Boolean {
        TODO("Not yet implemented")
    }

    fun setShouldReturnError(value: Boolean){
        shouldReturnError = value
    }
}