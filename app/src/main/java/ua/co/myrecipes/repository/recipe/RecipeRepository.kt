package ua.co.myrecipes.repository.recipe

import androidx.core.net.toUri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ua.co.myrecipes.di.AppModule
import ua.co.myrecipes.di.AppModule.Recipes
import ua.co.myrecipes.di.AppModule.Users
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.AuthUtil.Companion.uid
import ua.co.myrecipes.util.RecipeType
import ua.co.myrecipes.util.Resource
import ua.co.myrecipes.util.dataCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    @Recipes private val recipeRef: CollectionReference,
    @Users private val userRef: CollectionReference
): RecipeRepositoryInt{

    override suspend fun getRecipesByType(recipeType: RecipeType) = withContext(Dispatchers.IO) {
        dataCall {
            val recipes = recipeRef.whereEqualTo("type", recipeType.name)
                .get()
                .await()
                .toObjects(Recipe::class.java)
            Resource.Success(recipes)
        }
    }

    override suspend fun getCurrentUserRecipes() = withContext(Dispatchers.IO) {
        dataCall {
            val userRecipesIDs = userRef.document(uid).get().await().toObject(User::class.java)?.recipes
            val userRecipes = userRecipesIDs?.map { id ->
                recipeRef.whereEqualTo("id", id).get().await().first().toObject(Recipe::class.java)
            } ?: listOf()
            Resource.Success(userRecipes)
        }
    }

    override suspend fun getMyLikedRecipes() = withContext(Dispatchers.IO) {
        dataCall {
            val myLikedIds = userRef.document(uid).get().await().toObject(User::class.java)?.likedRecipes
            val likedRecipes = myLikedIds?.map { id ->
                recipeRef.whereEqualTo("id", id).get().await().first().toObject(Recipe::class.java)
            } ?: listOf()
            Resource.Success(likedRecipes)
        }
    }

    override suspend fun getRecipesByUserName(userName: String) = withContext(Dispatchers.IO) {
        dataCall {
            val userRecipesIDs = userRef.whereEqualTo("nickname", userName)
                .get().await().first().toObject(User::class.java).recipes
            val likedRecipes = userRecipesIDs.map { id ->
                recipeRef.whereEqualTo("id", id).get().await().first().toObject(Recipe::class.java)
            }
            Resource.Success(likedRecipes)
        }
    }

    override suspend fun getRecipe(recipe: Recipe) =  withContext(Dispatchers.IO) {
        dataCall {
            val recipeItem = recipeRef.document(recipe.id).get().await().toObject(Recipe::class.java)!!
            Resource.Success(recipeItem)
        }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        val imageUploadResult = Firebase.storage.reference.child("images/${recipe.id}").putFile(recipe.imgUrl.toUri()).await()
        recipe.imgUrl = imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()

        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val userRecipes = transaction.get(userRef.document(uid)).get("recipes") as MutableList<String>
            userRecipes.add(recipe.id)
            transaction.update(userRef.document(uid),"recipes",userRecipes)

            transaction.set(recipeRef.document(recipe.id),recipe)
            null
        }.addOnFailureListener {
            Firebase.storage.reference.child("images/${recipe.id}").delete()
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
            val userRecipes = transaction.get(userRef.document(uid)).get("recipes")!! as MutableList<String>
            userRecipes.remove(recipe.id)
            transaction.update(userRef.document(uid),"recipes",userRecipes)

            transaction.delete(recipeRef.document(recipe.id))
            null
        }.addOnSuccessListener {
            Firebase.storage.reference.child("images/${recipe.id}").delete()
        }.await()
    }

    override suspend fun toggleLikeForRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        dataCall {
            var isLiked = false
            FirebaseFirestore.getInstance().runTransaction { transaction ->
                val recipeResult = transaction.get(recipeRef.document(recipe.id))
                val currentLikes = recipeResult.toObject(Recipe::class.java)?.likedBy ?: listOf()
                val likedRecipes = transaction.get(userRef.document(uid)).toObject(User::class.java)?.likedRecipes ?: listOf()
                transaction.update(
                    recipeRef.document(recipe.id),
                    "likedBy",
                    if (uid in currentLikes) currentLikes - uid
                    else {
                        isLiked = true
                        currentLikes + uid
                    }
                )
                transaction.update(
                    userRef.document(uid),
                    "likedRecipes",
                    if (recipe.id in likedRecipes) likedRecipes - recipe.id
                    else likedRecipes + recipe.id
                )
            }.await()
            Resource.Success(isLiked)
        }
    }
}
