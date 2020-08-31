package ua.co.myrecipes.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ua.co.myrecipes.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val collectionReference: CollectionReference,
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun registerUser(email: String, password: String): AuthResult{
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        collectionReference.document(email).set(
            User(email, password)
        )
        return result
    }

    suspend fun signInUser(email: String, password: String) = firebaseAuth.signInWithEmailAndPassword(email, password).await()



}