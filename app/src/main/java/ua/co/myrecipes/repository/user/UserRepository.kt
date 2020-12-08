package ua.co.myrecipes.repository.user

import android.graphics.Bitmap
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.Resource
import ua.co.myrecipes.util.authCall
import ua.co.myrecipes.util.dataCall
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val collectionReference: CollectionReference,
    private val firebaseAuth: FirebaseAuth
): UserRepositoryInt {


    override suspend fun register(email: String, password: String): Resource<AuthResult> = withContext(Dispatchers.IO) {
        authCall {
            val user = User(email = email)
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                user.token = it.token
            }.await()
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    user.uid = firebaseAuth.uid ?: UUID.randomUUID().toString()
                    collectionReference.document(user.uid).set(user)
                    firebaseAuth.currentUser?.sendEmailVerification()
                    firebaseAuth.signOut()
                }.await()
            Resource.Error("ERROR_ACTIVATION_LINK_SENT_TO_YOU", null)
        }
    }

    override suspend fun login(email: String, password: String, token: String): Resource<AuthResult> = withContext(Dispatchers.IO) {
        authCall {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    updateToken(token)
                }
            }.await()
            if (firebaseAuth.currentUser?.isEmailVerified == false) {
                Resource.Error("EMAIL_IS_NOT_ACTIVATED", null)
            } else {
                Resource.Success(result)
            }
        }
    }

    override suspend fun getUserToken(nickName: String): String =
        collectionReference.whereEqualTo("nickname",nickName).get().await().first()?.get("token", String::class.java)!!

    override suspend fun getUserByName(userName: String) = withContext(Dispatchers.IO) {
        dataCall {
            val user = collectionReference.whereEqualTo("nickname", userName).get().await().first().toObject(User::class.java)
            Resource.Success(user)
        }
    }

    override suspend fun getCurrentUser() = withContext(Dispatchers.IO) {
        dataCall {
            val user = collectionReference.document(firebaseAuth.uid!!).get().await().toObject(User::class.java)!!
            Resource.Success(user)
        }
    }

    override suspend fun updateAbout(about: String){
        collectionReference.document(firebaseAuth.uid!!).update("about",about)
    }

    override suspend fun updateImage(imgBitmap: Bitmap){
        val byteArray = compressBitmap(imgBitmap)
        val snapshot = Firebase.storage.reference.child("avatars/${firebaseAuth.uid?.substringBefore("@")}").putBytes(byteArray).await()
        val url = snapshot.storage.downloadUrl.await()
        val imgUrl = url.toString()
        collectionReference.document(firebaseAuth.uid!!).update("img", imgUrl)
    }

    private fun compressBitmap(bitmap: Bitmap):ByteArray{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
        return stream.toByteArray()
    }

    private fun updateToken(token: String){
        collectionReference.document(firebaseAuth.uid!!).update("token",token)
    }
}