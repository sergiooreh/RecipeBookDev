package ua.co.myrecipes.repository.user

import android.graphics.Bitmap
import com.google.firebase.auth.AuthResult
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.Resource

class FakeUserRepositoryTest: UserRepositoryInt {
    private val userList = mutableListOf<User>()
    private var shouldReturnError = false
    override suspend fun register(email: String, password: String): Resource<AuthResult> {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, password: String, token: String): Resource<AuthResult> {
        TODO("Not yet implemented")
    }


    override suspend fun getUserToken(nickName: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun getUserByName(userName: String): Resource<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentUser(): Resource<User> {
        TODO("Not yet implemented")
    }


    override suspend fun updateAbout(about: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateImage(imgBitmap: Bitmap) {
        TODO("Not yet implemented")
    }
}