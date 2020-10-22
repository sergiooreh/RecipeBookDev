package ua.co.myrecipes.repository.user

import android.graphics.Bitmap
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.DataState

class FakeUserRepositoryTest: UserRepositoryInt {
    private val userList = mutableListOf<User>()
    private var shouldReturnError = false
    override suspend fun registerUser(email: String, password: String): AuthResult {
        TODO("Not yet implemented")
    }

    override suspend fun signInUser(email: String, password: String, token: String): AuthResult {
        TODO("Not yet implemented")
    }

    override suspend fun logOut() {
        TODO("Not yet implemented")
    }

    override fun getUserEmail(): String {
        TODO("Not yet implemented")
    }

    override suspend fun getUserImg(): String {
        TODO("Not yet implemented")
    }

    override suspend fun getUserToken(nickName: String): String {
        TODO("Not yet implemented")
    }

    override fun getUser(userName: String): Flow<DataState<User>> {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): Flow<DataState<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateAbout(about: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateImage(imgBitmap: Bitmap) {
        TODO("Not yet implemented")
    }

    override suspend fun updateToken(token: String) {
        TODO("Not yet implemented")
    }


}