package ua.co.myrecipes.repository.user

import android.graphics.Bitmap
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.Resource

interface UserRepositoryInt {
    suspend fun register(email: String, password: String): Resource<String>

    suspend fun login(email: String, password: String, token: String): Resource<String>

    suspend fun logOut()

    fun getUserEmail(): String

    suspend fun getUserImg(): String

    suspend fun getUserToken(nickName: String): String

    fun getUserByName(userName: String): Flow<DataState<User>>

    fun getCurrentUser(): Flow<DataState<User>>

    suspend fun updateAbout(about: String)

    suspend fun updateImage(imgBitmap: Bitmap)

}