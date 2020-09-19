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
            val recipes = recipeRef.document(recipeType.name).collection("Recipe").get().await()
            emit(DataState.Success(recipes.toObjects(Recipe::class.java)))

            //
            val a = loadRecipesByAuthor()
            val aa = 21
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override fun loadRecipesByAuthor() = flow<DataState<List<Recipe>>> {
        emit(DataState.Loading)
        val list = mutableListOf<Recipe>()
        try {
            val ids = userRef.document(firebaseAuth.currentUser?.email!!).get().await().get("recipe",List::class.java)
            for (id in ids!!){
                list.add(recipeRef.whereEqualTo("id",id).get().await().first().toObject(Recipe::class.java))
            }
            emit(DataState.Success(list))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override fun loadRecipe(recipe: Recipe) =  flow<DataState<Recipe>> {
        emit(DataState.Loading)
        try {
            val recipeItem = recipeRef.document(recipe.type.name).collection("Recipe")
                .document(recipe.name).get().await()
            emit(DataState.Success(recipeItem.toObject(Recipe::class.java)!!))
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
        userRef.document(firebaseAuth.currentUser?.email!!).update("recipe", FieldValue.arrayUnion(recipe.id))
        recipeRef.document(recipe.type.name).collection("Recipe").document(recipe.name).set(recipe)
    }

    private suspend fun increaseCount(): Int?{
        val incrementId = statRef.document("Recipe").get().await().get("Count", Int::class.java)?.plus(1)
        statRef.document("Recipe").update("Count", incrementId)
        return incrementId
    }

    private fun compressBitmap(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
        return stream.toByteArray()
    }
}
