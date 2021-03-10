package ua.co.myrecipes.repository.recipe

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.AuthUtil.Companion.email
import ua.co.myrecipes.util.RecipeType
import ua.co.myrecipes.util.Resource

class FakeRecipeRepositoryTest: RecipeRepositoryInt{
    private val recipeList = mutableListOf<Recipe>()
    private var shouldReturnError = false

    override suspend fun getRecipesByType(recipeType: RecipeType): Resource<List<Recipe>> = withContext(Dispatchers.IO) {
        if (shouldReturnError){
            Resource.Error("Error while loading recipes", null)
        } else{
            Resource.Success(recipeList.filter { it.type == recipeType })
        }
    }

    override suspend fun getCurrentUserRecipes(): Resource<List<Recipe>> {
        return Resource.Success(recipeList.filter { it.author == email.substringBefore("@") })
    }

    override suspend fun getMyLikedRecipes(): Resource<List<Recipe>> {
        return Resource.Error("There is no my liked recipes")
    }

    override suspend fun getRecipe(recipe: Recipe): Resource<Recipe> {
        return Resource.Success(recipeList.find { it == recipe }!!)
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        recipeList.add(recipe)
    }

    override suspend fun getRecipesByUserName(userName: String): Resource<List<Recipe>> {
        return Resource.Success(recipeList.filter { it.author == userName })
    }


    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeList.remove(recipe)
    }

    override suspend fun toggleLikeForRecipe(recipe: Recipe): Resource<Boolean> {
        return Resource.Success(recipeList.find { it == recipe }?.isLiked!!)
    }

    fun setShouldReturnError(value: Boolean){
        shouldReturnError = value
    }
}