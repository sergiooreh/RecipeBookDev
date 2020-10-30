package ua.co.myrecipes.repository.recipe

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
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
    private val firebaseAuth: FirebaseAuth
): RecipeRepositoryInt{

    override fun getRecipesByType(recipeType: RecipeType) = flow<DataState<List<Recipe>>> {
        emit(DataState.Loading)
        try {
            val recipes = recipeRef.document(recipeType.name).collection(RECIPE_F).get().await().toObjects(Recipe::class.java)
            emit(DataState.Success(recipes))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override fun getCurrentUserRecipes() = flow {
        emit(DataState.Loading)
        try {
            val userRecipesIDs = (userRef.document(firebaseAuth.currentUser?.email!!).get().await().get("recipe") as HashMap<*, *>)
            val userRecipes = userRecipesIDs.map {
                recipeRef.document(it.value.toString()).collection(RECIPE_F)
                    .document(it.key.toString()).get().await().toObject(Recipe::class.java)!!
            }
            emit(DataState.Success(userRecipes))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override fun getMyLikedRecipes() = flow {
        emit(DataState.Loading)
        try {
            val likedRecipesIDs = (userRef.document(firebaseAuth.currentUser?.email!!).get().await().get("likedRecipes") as HashMap<*, *>)
            val likedRecipes = likedRecipesIDs.map {
                recipeRef.document(it.value.toString()).collection(RECIPE_F)
                    .document(it.key.toString()).get().await().toObject(Recipe::class.java)!!
            }
            emit(DataState.Success(likedRecipes))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override fun getRecipesByUserName(userName: String) = flow {
        emit(DataState.Loading)
        try {
            val userRecipesIDs = (userRef.whereEqualTo("nickname",userName).get().await().first().get("recipe") as HashMap<*, *>)
            val userRecipes = userRecipesIDs.map {
                recipeRef.document(it.value.toString()).collection(RECIPE_F)
                    .document(it.key.toString()).get().await().toObject(Recipe::class.java)!!
            }
            emit(DataState.Success(userRecipes))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }
    //? TODO: MAYBE to LiveData
    override fun getRecipe(recipe: Recipe) =  flow {
        emit(DataState.Loading)
        try {
            val recipeItem = recipeRef.document(recipe.type.name).collection(RECIPE_F)
                .document(recipe.id.toString()).get().await().toObject(Recipe::class.java)!!
            emit(DataState.Success(recipeItem))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        val byteArray = compressBitmap(recipe.imgBitmap!!)
        recipe.imgUrl.let {                                                                                                    //!!!!
            val snapshot = Firebase.storage.reference.child("images/${recipe.name}").putBytes(byteArray).await()
            val url = snapshot.storage.downloadUrl
            while (!url.isSuccessful);
            recipe.imgUrl = url.result.toString()
        }
        recipe.id = incrementID()!!

        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val userRecipes = transaction.get(userRef.document(firebaseAuth.currentUser?.email!!)).get("recipe")!! as HashMap<String,String>
            userRecipes[recipe.id.toString()] = recipe.type.name
            transaction.update(userRef.document(firebaseAuth.currentUser?.email!!),"recipe",userRecipes)

            transaction.set(recipeRef.document(recipe.type.name).collection(RECIPE_F).document(recipe.id.toString()),recipe)
            null
        }.addOnFailureListener {
            Firebase.storage.reference.child("images/${recipe.name}").delete()
        }
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

    private suspend fun incrementID(): Int?{
        var incrementedId: Int? = 0
        statRef.firestore.runTransaction { transaction ->
            incrementedId = transaction.get(statRef.document(RECIPE_F)).getField<Int>(COUNT_F)?.plus(1)
            transaction.update(statRef.document(RECIPE_F), COUNT_F, incrementedId)
            null
        }.await()
        return incrementedId
    }

    private fun compressBitmap(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        return stream.toByteArray()
    }
}
