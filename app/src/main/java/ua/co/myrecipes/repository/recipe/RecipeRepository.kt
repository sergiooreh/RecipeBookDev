package ua.co.myrecipes.repository.recipe

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.Constants.RECIPE_F
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeRef: CollectionReference,
    private val userRef: CollectionReference,
    private val firebaseAuth: FirebaseAuth
): RecipeRepositoryInt{

    override fun getRecipesByType(recipeType: RecipeType) = flow<DataState<List<Recipe>>> {
        emit(DataState.Loading)
        try {
            val recipes = recipeRef.document(recipeType.name).collection(RECIPE_F).get().await().toObjects(Recipe::class.java)
            emit(DataState.Success(recipes))
        } catch (e: FirebaseFirestoreException){
            emit(DataState.Error(e))
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe){
        val usersLikedIDs = recipeRef.document(recipe.type.name).collection(RECIPE_F).document(recipe.id).get().await().get("userLiked") as MutableList<String>
        usersLikedIDs.onEach {
            val userLikedRecipes = userRef.document(it).get().await().get("likedRecipes") as HashMap<String, String>
            userLikedRecipes.remove(recipe.id)
            userRef.document(it).update("likedRecipes", userLikedRecipes)
        }
        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val userRecipes = transaction.get(userRef.document(firebaseAuth.currentUser?.uid!!)).get("recipe")!! as HashMap<String,String>
            userRecipes.remove(recipe.id)
            transaction.update(userRef.document(firebaseAuth.currentUser?.uid!!),"recipe",userRecipes)

            transaction.delete(recipeRef.document(recipe.type.name).collection(RECIPE_F).document(recipe.id))
            null
        }.addOnSuccessListener {
            Firebase.storage.reference.child("images/${recipe.name}").delete()
        }.await()
    }

    override fun getCurrentUserRecipes() = flow {
        emit(DataState.Loading)
        try {
            val userRecipesIDs = (userRef.document(firebaseAuth.currentUser?.uid!!).get().await().get("recipe") as HashMap<*, *>)
            val userRecipes = userRecipesIDs.map {
                recipeRef.document(it.value.toString()).collection(RECIPE_F)
                    .document(it.key.toString()).get().await().toObject(Recipe::class.java)!!
            }
            emit(DataState.Success(userRecipes))
        } catch (e: FirebaseFirestoreException){
            emit(DataState.Error(e))
        }
    }

    override fun getMyLikedRecipes() = flow {
        emit(DataState.Loading)
        try {
            val likedRecipesIDs = (userRef.document(firebaseAuth.currentUser?.uid!!).get().await().get("likedRecipes") as HashMap<*, *>)
            val likedRecipes = likedRecipesIDs.map {
                recipeRef.document(it.value.toString()).collection(RECIPE_F)
                    .document(it.key.toString()).get().await().toObject(Recipe::class.java)!!
            }
            emit(DataState.Success(likedRecipes))
        } catch (e: FirebaseFirestoreException){
            emit(DataState.Error(e))
        }
    }
//TODO
    override fun getRecipesByUserName(userName: String) = flow {
        emit(DataState.Loading)
        try {
            val userRecipesIDs = (userRef.whereEqualTo("nickname",userName).get().await().first().get("recipe") as HashMap<*, *>)
            val userRecipes = userRecipesIDs.map {
                recipeRef.document(it.value.toString()).collection(RECIPE_F)
                    .document(it.key.toString()).get().await().toObject(Recipe::class.java)!!
            }
            emit(DataState.Success(userRecipes))
        } catch (e: FirebaseFirestoreException){
            emit(DataState.Error(e))
        }
    }

    override fun getRecipe(recipe: Recipe) =  flow {
        emit(DataState.Loading)
        try {
            val recipeItem = recipeRef.document(recipe.type.name).collection(RECIPE_F)
                .document(recipe.id).get().await().toObject(Recipe::class.java)!!
            emit(DataState.Success(recipeItem))
        } catch (e: FirebaseFirestoreException){
            emit(DataState.Error(e))
        }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        val byteArray = compressBitmap(recipe.imgBitmap!!)
        recipe.imgUrl.let {
            val snapshot = Firebase.storage.reference.child("images/${recipe.name}").putBytes(byteArray).await()
            val url = snapshot.storage.downloadUrl.await()
            recipe.imgUrl = url.toString()
        }

        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val userRecipes = transaction.get(userRef.document(firebaseAuth.currentUser?.uid!!)).get("recipe")!! as HashMap<String,String>
            userRecipes[recipe.id] = recipe.type.name
            transaction.update(userRef.document(firebaseAuth.currentUser?.uid!!),"recipe",userRecipes)

            transaction.set(recipeRef.document(recipe.type.name).collection(RECIPE_F).document(recipe.id),recipe)
            null
        }.addOnFailureListener {
            Firebase.storage.reference.child("images/${recipe.name}").delete()
        }
    }

    override suspend fun addLikedRecipe(recipe: Recipe){
        val userRecipes = (userRef.document(firebaseAuth.currentUser?.uid!!).get().await().get("likedRecipes") as HashMap<String, String>)
        userRecipes[recipe.id] = recipe.type.name
        userRef.document(firebaseAuth.currentUser?.uid!!).update("likedRecipes", userRecipes)
        val usersLiked = recipeRef.document(recipe.type.name).collection(RECIPE_F).document(recipe.id).get().await().get("userLiked") as MutableList<String>
        usersLiked.add(firebaseAuth.currentUser?.uid!!)
        recipeRef.document(recipe.type.name).collection(RECIPE_F).document(recipe.id).update("userLiked",usersLiked)
    }

    override suspend fun removeLikedRecipe(recipe: Recipe){
        val userRecipes = (userRef.document(firebaseAuth.currentUser?.uid!!).get().await().get("likedRecipes") as HashMap<String, String>)
        userRecipes.remove(recipe.id)
        userRef.document(firebaseAuth.currentUser?.uid!!).update("likedRecipes", userRecipes)
        val usersLiked = recipeRef.document(recipe.type.name).collection(RECIPE_F).document(recipe.id).get().await().get("userLiked") as MutableList<String>
        usersLiked.remove(firebaseAuth.currentUser?.uid!!)
        recipeRef.document(recipe.type.name).collection(RECIPE_F).document(recipe.id).update("userLiked",usersLiked)
    }

    override suspend fun isLikedRecipe(recipe: Recipe): Boolean{
        val userRecipes = (userRef.document(firebaseAuth.currentUser?.uid!!).get().await().get("likedRecipes") as HashMap<String, String>)
        return userRecipes.keys.contains(recipe.id)
    }

    private fun compressBitmap(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        return stream.toByteArray()
    }
}
