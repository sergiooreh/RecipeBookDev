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

    override fun getUser() = flow {
        emit(DataState.Loading)
        try {
            val user = collectionReference.document(getUserEmail()).get().await().toObject(User::class.java)
            emit(DataState.Success(user!!))
        } catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    override suspend fun logOut() = firebaseAuth.signOut()


}