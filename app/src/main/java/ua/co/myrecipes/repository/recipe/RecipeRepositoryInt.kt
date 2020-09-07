package ua.co.myrecipes.repository.recipe

import kotlinx.coroutines.flow.Flow
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType

interface RecipeRepositoryInt {

    fun loadRecipes(recipeType: RecipeType): Flow<DataState<List<Recipe>>>

    suspend fun addRecipe(recipe: Recipe)
}