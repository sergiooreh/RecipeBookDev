package ua.co.myrecipes.repository

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ua.co.myrecipes.db.recipes.RecipeCacheMapper
import ua.co.myrecipes.db.recipes.RecipeDao
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val collectionReference: CollectionReference,
    private val recipeCacheMapper: RecipeCacheMapper,
    private val recipeDao: RecipeDao
){
    /*suspend fun loadRecipeTypes(recipe: String, hasInternet: Boolean): Flow<DataState<List<Recipe>>> = flow {
        emit(DataState.Loading)
        try {
            if (hasInternet) {
                val objects = collectionReference.document(recipe).collection("objects").get().await()
                for (objct in objects) {
                    recipeDao.insert(recipeCacheMapper.mapToEntity(objct.toObject(Recipe::class.java)))
                }
            }
            emit(DataState.Success(recipeCacheMapper.mapFromEntityList(recipeDao.getByName(recipe))))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }*/

    suspend fun addRecipe(recipe: Recipe){
        collectionReference.document(recipe.type.name).collection("Recipe").document(recipe.name).set(Recipe::class)
    }
}