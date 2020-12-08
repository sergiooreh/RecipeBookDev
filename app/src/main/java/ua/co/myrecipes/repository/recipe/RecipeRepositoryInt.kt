package ua.co.myrecipes.repository.recipe

import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.RecipeType
import ua.co.myrecipes.util.Resource

interface RecipeRepositoryInt {

    suspend fun getRecipesByType(recipeType: RecipeType): Resource<List<Recipe>>

    suspend fun getCurrentUserRecipes(): Resource<List<Recipe>>

    suspend fun getMyLikedRecipes(): Resource<List<Recipe>>

    suspend fun getRecipe(recipe: Recipe): Resource<Recipe>

    suspend fun insertRecipe(recipe: Recipe)

    suspend fun getRecipesByUserName(userName: String): Resource<List<Recipe>>

    suspend fun deleteRecipe(recipe: Recipe)

    suspend fun toggleLikeForRecipe(recipe: Recipe): Resource<Boolean>
}