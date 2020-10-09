package ua.co.myrecipes.repository.user

import android.graphics.Bitmap
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.DataState
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val collectionReference: CollectionReference,
    private val firebaseAuth: FirebaseAuth
): UserRepositoryInt {

    override suspend fun registerUser(email: String, password: String): AuthResult =
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            collectionReference.document(email).set(User(email, password))}.await()

    override suspend fun signInUser(email: String, password: String): AuthResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()

    override fun getUserEmail() = firebaseAuth.currentUser?.email ?: ""

    override suspend fun getUserImg() = (collectionReference.document(firebaseAuth.currentUser?.email!!).get().await().get("img") as String)

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

    override fun updateAbout(about: String){
        collectionReference.document(getUserEmail()).update("about",about)
    }

    override suspend fun updateImage(imgBitmap: Bitmap){
        val byteArray = compressBitmap(imgBitmap)
        val snapshot = Firebase.storage.reference.child("avatars/${firebaseAuth.currentUser?.email?.substringBefore("@")}").putBytes(byteArray).await()
        val url = snapshot.storage.downloadUrl
        while (!url.isSuccessful);
        val imgUrl = url.result.toString()
        collectionReference.document(firebaseAuth.currentUser?.email!!).update("img", imgUrl)
    }

    override suspend fun logOut() = firebaseAuth.signOut()

    private fun compressBitmap(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
        return stream.toByteArray()
    }

}