package ua.co.myrecipes.repository.recipe

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Assert.*
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType
import java.lang.Exception

class FakeRecipeRepositoryTest: RecipeRepositoryInt{
    private val recipeList = mutableListOf<Recipe>()
    private var shouldReturnError = false

    override fun loadRecipes(recipeType: RecipeType): Flow<DataState<List<Recipe>>>  = flow {
        if (shouldReturnError){
            emit(DataState.Error(Exception("Error while loading recipes")))
        } else{
            emit(DataState.Success(recipeList))
        }
    }

    override suspend fun addRecipe(recipe: Recipe) {
        recipeList.add(recipe)
    }

    fun setShouldReturnError(value: Boolean){
        shouldReturnError = value
    }
}