package ua.co.myrecipes.repository.recipe

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.RecipeType
import ua.co.myrecipes.util.Resource

class FakeRecipeRepositoryTest: RecipeRepositoryInt{
    private val recipeList = mutableListOf<Recipe>()
    private var shouldReturnError = false

    override suspend fun getRecipesByType(recipeType: RecipeType): Resource<List<Recipe>> = withContext(Dispatchers.IO) {
        if (shouldReturnError){
            Resource.Error("Error while loading recipes", null)
        } else{
            Resource.Success(recipeList)
        }
    }

    override suspend fun getCurrentUserRecipes(): Resource<List<Recipe>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyLikedRecipes(): Resource<List<Recipe>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecipe(recipe: Recipe): Resource<Recipe> {
        TODO("Not yet implemented")
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun getRecipesByUserName(userName: String): Resource<List<Recipe>> {
        TODO("Not yet implemented")
    }


    override suspend fun deleteRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun toggleLikeForRecipe(recipe: Recipe): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    fun setShouldReturnError(value: Boolean){
        shouldReturnError = value
    }
}