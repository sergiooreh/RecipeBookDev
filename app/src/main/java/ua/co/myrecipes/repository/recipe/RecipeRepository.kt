package ua.co.myrecipes.repository.recipe

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ua.co.myrecipes.db.recipes.RecipeCacheMapper
import ua.co.myrecipes.db.recipes.RecipeDao
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.Constants.COUNT_F
import ua.co.myrecipes.util.Constants.RECIPE_F
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeRef: CollectionReference,
    private val statRef: CollectionReference,
    private val userRef: CollectionReference,
    private val firebaseAuth: FirebaseAuth,
    private val recipeCacheMapper: RecipeCacheMapper,
    private val recipeDao: RecipeDao
): RecipeRepositoryInt{

    override fun loadRecipesByType(recipeType: RecipeType) = flow<DataState<List<Recipe>>> {
        emit(DataState.Loading)
        try {
            val recipes = recipeRef.document(recipeType.name).collection(RECIPE_F).get().await()
            emit(DataState.Success(recipes.toObjects(Recipe::class.java)))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override fun loadRecipesCurrentUser() = flow {
        emit(DataState.Loading)
        try {
            val ids = (userRef.document(firebaseAuth.currentUser?.email!!).get().await().get("recipe") as HashMap<*, *>)
            val list = ids.map {
                recipeRef.document(it.value.toString()).collection(RECIPE_F)
                    .document(it.key.toString()).get().await().toObject(Recipe::class.java)!!
            }
            emit(DataState.Success(list))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override fun loadMyLikedRecipes() = flow {
        emit(DataState.Loading)
        try {
            val ids = (userRef.document(firebaseAuth.currentUser?.email!!).get().await().get("likedRecipes") as HashMap<*, *>)
            val list = ids.map {
                recipeRef.document(it.value.toString()).collection(RECIPE_F)
                    .document(it.key.toString()).get().await().toObject(Recipe::class.java)!!
            }
            emit(DataState.Success(list))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override fun loadRecipesUser(userName: String) = flow {
        emit(DataState.Loading)
        try {
            val ids = (userRef.whereEqualTo("nickname",userName).get().await().first().get("recipe") as HashMap<*, *>)
            val list = ids.map {
                recipeRef.document(it.value.toString()).collection(RECIPE_F)
                    .document(it.key.toString()).get().await().toObject(Recipe::class.java)!!
            }
            emit(DataState.Success(list))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override fun loadRecipe(recipe: Recipe) =  flow {
        emit(DataState.Loading)
        try {
            val recipeItem = recipeRef.document(recipe.type.name).collection(RECIPE_F)
                .document(recipe.id.toString()).get().await().toObject(Recipe::class.java)!!
            emit(DataState.Success(recipeItem))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override suspend fun addRecipe(recipe: Recipe) {
        val byteArray = compressBitmap(recipe.imgBitmap!!)
        recipe.img.let {
            val snapshot = Firebase.storage.reference.child("images/${recipe.name}").putBytes(byteArray).await()
            val url = snapshot.storage.downloadUrl
            while (!url.isSuccessful);
            recipe.img = url.result.toString()
        }
        recipe.id = increaseCount()!!

        val userRecipes = (userRef.document(firebaseAuth.currentUser?.email!!).get().await().get("recipe") as HashMap<String, String>)
        userRecipes[recipe.id.toString()] = recipe.type.name
        userRef.document(firebaseAuth.currentUser?.email!!).update("recipe", userRecipes)

        recipeRef.document(recipe.type.name).collection(RECIPE_F).document(recipe.id.toString()).set(recipe)
    }

    override suspend fun addLikedRecipe(recipe: Recipe){
        val userRecipes = (userRef.document(firebaseAuth.currentUser?.email!!).get().await().get("likedRecipes") as HashMap<String, String>)
        userRecipes[recipe.id.toString()] = recipe.type.name
        userRef.document(firebaseAuth.currentUser?.email!!).update("likedRecipes", userRecipes)
    }

    override suspend fun removeLikedRecipe(recipe: Recipe){
        val userRecipes = (userRef.document(firebaseAuth.currentUser?.email!!).get().await().get("likedRecipes") as HashMap<String, String>)
        userRecipes.remove(recipe.id.toString())
        userRef.document(firebaseAuth.currentUser?.email!!).update("likedRecipes", userRecipes)
    }

    override suspend fun isLikedRecipe(recipe: Recipe): Boolean{
        val userRecipes = (userRef.document(firebaseAuth.currentUser?.email!!).get().await().get("likedRecipes") as HashMap<String, String>)
        return userRecipes.keys.contains(recipe.id.toString())
    }

    private suspend fun increaseCount(): Int?{
        val incrementId = statRef.document(RECIPE_F).get().await().get(COUNT_F, Int::class.java)?.plus(1)
        statRef.document(RECIPE_F).update(COUNT_F, incrementId)
        return incrementId
    }

    private fun compressBitmap(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
        return stream.toByteArray()
    }
}
