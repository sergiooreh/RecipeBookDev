package ua.co.myrecipes.repository.user

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.DataState
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val collectionReference: CollectionReference,
    private val firebaseAuth: FirebaseAuth
): UserRepositoryInt {

    override suspend fun registerUser(email: String, password: String): AuthResult{
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        collectionReference.document(email).set(User(email, password))
        return result
    }

    override suspend fun signInUser(email: String, password: String): AuthResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()

    override fun getUserEmail() = firebaseAuth.currentUser?.email ?: ""

    override fun getUser(userName: String) = flow {
        emit(DataState.Loading)
        try {
            val user = collectionReference.whereEqualTo("nickname", userName).get().await().first().toObject(User::class.java)
            emit(DataState.Success(user))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override fun getCurrentUser() = flow {
        emit(DataState.Loading)
        try {
            val user = collectionReference.document(getUserEmail()).get().await().toObject(User::class.java)
            emit(DataState.Success(user!!))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override suspend fun followUser(userName: String) {
        val userRecipes = (collectionReference.document(firebaseAuth.currentUser?.email!!).get().await().get("following") as List<String>)
        userRecipes.toMutableList().add(userName)
        collectionReference.document(firebaseAuth.currentUser?.email!!).update("following", userRecipes)

        getFollower(userName)
    }

    fun getFollower(userName: String){
        collectionReference.document()
    }

    override suspend fun logOut() = firebaseAuth.signOut()


}