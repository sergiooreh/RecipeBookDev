package ua.co.myrecipes.repository.user

import android.graphics.Bitmap
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.Resource

class FakeUserRepositoryTest: UserRepositoryInt {
    private val userList = mutableListOf<User>()
    private var shouldReturnError = false
    override suspend fun register(email: String, password: String): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, password: String, token: String): Resource<String> {
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

    override fun getUserByName(userName: String): Flow<DataState<User>> {
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


}