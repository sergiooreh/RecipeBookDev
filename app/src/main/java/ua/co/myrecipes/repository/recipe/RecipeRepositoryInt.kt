package ua.co.myrecipes.repository.recipe

import kotlinx.coroutines.flow.Flow
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType

interface RecipeRepositoryInt {

    fun loadRecipesByType(recipeType: RecipeType): Flow<DataState<List<Recipe>>>

    fun loadRecipesCurrentUser(): Flow<DataState<List<Recipe>>>

    fun loadMyLikedRecipes(): Flow<DataState<List<Recipe>>>

    suspend fun addLikedRecipe(recipe: Recipe)
    suspend fun removeLikedRecipe(recipe: Recipe)

    fun loadRecipe(recipe: Recipe): Flow<DataState<Recipe>>

    suspend fun addRecipe(recipe: Recipe)

    fun loadRecipesUser(userName: String): Flow<DataState<List<Recipe>>>

    suspend fun isLikedRecipe(recipe: Recipe): Boolean
}