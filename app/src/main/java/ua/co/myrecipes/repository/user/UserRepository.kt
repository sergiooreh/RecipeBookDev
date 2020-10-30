package ua.co.myrecipes.repository.user

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.getField
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
import ua.co.myrecipes.util.Constants.COUNT_F
import ua.co.myrecipes.util.Constants.USER_F
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.Resource
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val collectionReference: CollectionReference,
    private val statReference: CollectionReference,
    private val firebaseAuth: FirebaseAuth
): UserRepositoryInt {

    override suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val user = User(email, password)
            user.id = increaseCount()!!
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                user.token = it.token
            }.await()
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    collectionReference.document(email).set(user)
                }.await()
            Resource.success("Successfully")
        } catch (e: Exception){
            Resource.error(e.message ?: "", null)
        }
    }

    override suspend fun login(email: String, password: String, token: String) = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    updateToken(token)
                }
            }.await()
            Resource.success("Successfully")
        } catch (e: Exception){
            Resource.error(e.message ?: "unkwno error", null)
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

        private suspend fun increaseCount(): Int?{
            var incrementedId: Int? = 0
            statReference.firestore.runTransaction { transaction ->
                incrementedId = transaction.get(statReference.document(USER_F)).getField<Int>(COUNT_F)?.plus(1)
                transaction.update(statReference.document(USER_F), COUNT_F, incrementedId)
                null
            }.await()
            return incrementedId
        }

        private suspend fun updateToken(token: String){
            collectionReference.whereEqualTo("email",getUserEmail()).get().await().first()?.reference?.update("token",token)
        }
    }