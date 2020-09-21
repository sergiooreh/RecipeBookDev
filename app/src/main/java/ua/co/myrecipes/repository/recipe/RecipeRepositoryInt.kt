package ua.co.myrecipes.repository.recipe

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType

interface RecipeRepositoryInt {

    fun loadRecipesByType(recipeType: RecipeType): Flow<DataState<List<Recipe>>>

    fun loadRecipesCurrentUser(): Flow<DataState<List<Recipe>>>

    fun loadRecipe(recipe: Recipe): Flow<DataState<Recipe>>

    suspend fun addRecipe(recipe: Recipe)

    fun loadRecipesUser(userName: String): Flow<DataState<List<Recipe>>>
}