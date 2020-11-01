package ua.co.myrecipes.repository.recipe

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType

interface RecipeRepositoryInt {

    fun getRecipesByType(recipeType: RecipeType): Flow<DataState<List<Recipe>>>

    fun getCurrentUserRecipes(): Flow<DataState<List<Recipe>>>

    fun getMyLikedRecipes(): Flow<DataState<List<Recipe>>>

    suspend fun addLikedRecipe(recipe: Recipe)
    suspend fun removeLikedRecipe(recipe: Recipe)

    fun getRecipe(recipe: Recipe): Flow<DataState<Recipe>>

    suspend fun insertRecipe(recipe: Recipe)

    fun getRecipesByUserName(userName: String): Flow<DataState<List<Recipe>>>

    suspend fun isLikedRecipe(recipe: Recipe): Boolean

    suspend fun deleteRecipe(recipe: Recipe)
}