package ua.co.myrecipes.repository.recipe

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.RecipeType
import ua.co.myrecipes.util.Resource
import ua.co.myrecipes.util.dataCall
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeRef: CollectionReference,
    private val userRef: CollectionReference,
    private val userUid: String
): RecipeRepositoryInt{

    override suspend fun getRecipesByType(recipeType: RecipeType) = withContext(Dispatchers.IO) {
        dataCall {
            val recipes = recipeRef.whereEqualTo("type", recipeType.name)
//                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Recipe::class.java)
            Resource.Success(recipes)
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe){
        val usersLikedIDs = recipeRef.document(recipe.id).get().await().get("likedBy") as MutableList<String>
        usersLikedIDs.onEach {
            val userLikedRecipes = userRef.document(it).get().await().get("likedRecipes") as MutableList<String>
            userLikedRecipes.remove(recipe.id)
            userRef.document(it).update("likedRecipes", userLikedRecipes)
        }
        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val userRecipes = transaction.get(userRef.document(userUid)).get("recipes")!! as MutableList<String>
            userRecipes.remove(recipe.id)
            transaction.update(userRef.document(userUid),"recipes",userRecipes)

            transaction.delete(recipeRef.document(recipe.id))
            null
        }.addOnSuccessListener {
            Firebase.storage.reference.child("images/${recipe.id}").delete()
        }.await()
    }

    override suspend fun getCurrentUserRecipes() = withContext(Dispatchers.IO) {
        dataCall {
            val userRecipesIDs = (userRef.document(userUid).get().await().get("recipes") as List<String>)
            val userRecipes = userRecipesIDs.map { id ->
                recipeRef.whereEqualTo("id", id).get().await().first().toObject(Recipe::class.java)
            }
            Resource.Success(userRecipes)
        }
    }

    override suspend fun getMyLikedRecipes() = withContext(Dispatchers.IO) {
        dataCall {
            val myLikedIds = (userRef.document(userUid).get().await().get("likedRecipes") as List<String>)
            val likedRecipes = myLikedIds.map { id ->
                recipeRef.whereEqualTo("id", id).get().await().first().toObject(Recipe::class.java)
            }
            Resource.Success(likedRecipes)
        }
    }

    override suspend fun getRecipesByUserName(userName: String) = withContext(Dispatchers.IO) {
        dataCall {
            val userRecipesIDs = (userRef.whereEqualTo("nickname",userName).get().await().first().get("recipes") as List<String>)
            val userRecipes = userRecipesIDs.map { id ->
                recipeRef.whereEqualTo("id", id).get().await().first().toObject(Recipe::class.java)
            }
            Resource.Success(userRecipes)
        }
    }

    override suspend fun getRecipe(recipe: Recipe) =  withContext(Dispatchers.IO) {
        dataCall {
            val recipeItem = recipeRef.document(recipe.id).get().await().toObject(Recipe::class.java)!!
            Resource.Success(recipeItem)
        }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        val byteArray = compressBitmap(recipe.imgBitmap!!)
        recipe.imgUrl.let {
            val snapshot = Firebase.storage.reference.child("images/${recipe.id}").putBytes(byteArray).await()
            val url = snapshot.storage.downloadUrl.await()
            recipe.imgUrl = url.toString()
        }

        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val userRecipes = transaction.get(userRef.document(userUid)).get("recipes") as MutableList<String>
            userRecipes.add(recipe.id)
            transaction.update(userRef.document(userUid),"recipes",userRecipes)

            transaction.set(recipeRef.document(recipe.id),recipe)
            null
        }.addOnFailureListener {
            Firebase.storage.reference.child("images/${recipe.id}").delete()
        }
    }

    override suspend fun toggleLikeForRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        dataCall {
            var isLiked = false
            FirebaseFirestore.getInstance().runTransaction { transaction ->
                val recipeResult = transaction.get(recipeRef.document(recipe.id))
                val currentLikes = recipeResult.toObject(Recipe::class.java)?.likedBy ?: listOf()
                val likedRecipes = transaction.get(userRef.document(userUid)).toObject(User::class.java)?.likedRecipes ?: listOf()
                transaction.update(
                    recipeRef.document(recipe.id),
                    "likedBy",
                    if (userUid in currentLikes) currentLikes - userUid
                    else {
                        isLiked = true
                        currentLikes + userUid
                    }
                )
                transaction.update(
                    userRef.document(userUid),
                    "likedRecipes",
                    if (recipe.id in likedRecipes) likedRecipes - recipe.id
                    else likedRecipes + recipe.id
                )
            }.await()
            Resource.Success(isLiked)
        }
    }

    private fun compressBitmap(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        return stream.toByteArray()
    }
}
