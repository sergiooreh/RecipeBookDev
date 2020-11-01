package ua.co.myrecipes.repository.user

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.Resource
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val collectionReference: CollectionReference,
    private val firebaseAuth: FirebaseAuth
): UserRepositoryInt {

    override suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val user = User(email, password)
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                user.token = it.token
            }.await()
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    user.id = firebaseAuth.currentUser?.uid ?: UUID.randomUUID().toString()
                    collectionReference.document(user.id).set(user)
                    firebaseAuth.currentUser?.sendEmailVerification()
                }.await()
            Resource.error("ERROR_ACTIVATION_LINK_SENT_TO_YOU", null)
        } catch (e: FirebaseAuthException){
            Resource.error(e.errorCode, null)
        }
    }

    override suspend fun login(email: String, password: String, token: String) = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    updateToken(token)
                }
            }.await()
            if (firebaseAuth.currentUser?.isEmailVerified == false) {
                Resource.error("EMAIL_IS_NOT_ACTIVATED", null)
            } else {
                Resource.success(null)
            }
        } catch (e: FirebaseAuthException){
            Resource.error(e.errorCode, null)
        }
    }

    override fun getUserEmail() = firebaseAuth.currentUser?.email ?: ""

    override suspend fun getUserImg() = withContext (Dispatchers.IO){
        collectionReference.whereEqualTo("email",getUserEmail()).get().await().first()?.get("img") as String
    }

        override suspend fun getUserToken(nickName: String): String =
            collectionReference.whereEqualTo("nickname",nickName).get().await().first()?.get("token", String::class.java)!!

        override fun getUserByName(userName: String) = flow {
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
                val user = collectionReference.whereEqualTo("email",getUserEmail()).get().await().first().toObject(User::class.java)
                emit(DataState.Success(user))
            } catch (e: Exception){
                emit(DataState.Error(e))
            }
        }

        override suspend fun updateAbout(about: String){
            collectionReference.whereEqualTo("email",getUserEmail()).get().await().first()?.reference?.update("about",about)
        }

        override suspend fun updateImage(imgBitmap: Bitmap){
            val byteArray = compressBitmap(imgBitmap)
            val snapshot = Firebase.storage.reference.child("avatars/${firebaseAuth.currentUser?.email?.substringBefore("@")}").putBytes(byteArray).await()
            val url = snapshot.storage.downloadUrl
            while (!url.isSuccessful);
            val imgUrl = url.result.toString()
            collectionReference.whereEqualTo("email",getUserEmail()).get().await().first()?.reference?.update("img", imgUrl)
        }

        override suspend fun logOut() = firebaseAuth.signOut()

        private fun compressBitmap(bitmap: Bitmap):ByteArray{
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
            return stream.toByteArray()
        }

        private suspend fun updateToken(token: String){
            collectionReference.whereEqualTo("email",getUserEmail()).get().await().first()?.reference?.update("token",token)
        }
    }