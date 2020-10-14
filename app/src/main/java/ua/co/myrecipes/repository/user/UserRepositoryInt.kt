package ua.co.myrecipes.repository.user

import android.graphics.Bitmap
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.DataState

interface UserRepositoryInt {
    suspend fun registerUser(email: String, password: String): AuthResult

    suspend fun signInUser(email: String, password: String, token: String): AuthResult

    suspend fun logOut()

    fun getUserEmail(): String

    suspend fun getUserImg(): String

    suspend fun getUserToken(nickName: String): String

    fun getUser(userName: String): Flow<DataState<User>>

    fun getCurrentUser(): Flow<DataState<User>>

    suspend fun updateAbout(about: String)

    suspend fun updateImage(imgBitmap: Bitmap)

    suspend fun updateToken(token: String)
}