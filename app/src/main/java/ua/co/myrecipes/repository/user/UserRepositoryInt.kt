package ua.co.myrecipes.repository.user

import android.graphics.Bitmap
import com.google.firebase.auth.AuthResult
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.Resource

interface UserRepositoryInt {
    suspend fun register(email: String, password: String): Resource<AuthResult>

    suspend fun login(email: String, password: String, token: String): Resource<AuthResult>

    suspend fun getUserToken(nickName: String): String

    suspend fun getUserByNickName(nickName: String): Resource<User>

    suspend fun getCurrentUser(): Resource<User>

    suspend fun updateAbout(about: String)

    suspend fun updateImage(imgBitmap: Bitmap)

}