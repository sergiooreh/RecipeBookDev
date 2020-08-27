package ua.co.myrecipes.repository

import android.graphics.Bitmap
import androidx.core.net.toUri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ua.co.myrecipes.db.recipes.RecipeCacheMapper
import ua.co.myrecipes.db.recipes.RecipeDao
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.RecipeType
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val collectionReference: CollectionReference,
    private val recipeCacheMapper: RecipeCacheMapper,
    private val recipeDao: RecipeDao
){
    fun loadRecipes(recipeType: RecipeType) = flow<List<Recipe>> {
        val list = mutableListOf<Recipe>()
        val recipes = collectionReference.document(recipeType.name).collection("Recipe").get().await()
        for (recipe in recipes){
            list.add(recipe.toObject(Recipe::class.java))
        }
        emit(list)
    }

    fun addRecipe(recipe: Recipe) = CoroutineScope(Dispatchers.IO).launch {
        val byteArray = compressBitmap(recipe.imgBitmap!!)
        recipe.img.let {
            val snapshot = Firebase.storage.reference.child("images/${recipe.name}").putBytes(byteArray).await()
                val url = snapshot.storage.downloadUrl
                while (!url.isSuccessful);
                recipe.img = url.result.toString()
        }
        collectionReference.document(recipe.type.name).collection("Recipe").document(recipe.name).set(recipe)
    }

    private fun compressBitmap(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, stream)
        return stream.toByteArray()
    }
}
