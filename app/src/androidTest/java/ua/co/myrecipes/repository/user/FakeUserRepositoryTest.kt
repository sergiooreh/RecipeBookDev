package ua.co.myrecipes.repository.user

import android.graphics.Bitmap
import com.google.firebase.auth.AuthResult
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.AuthUtil.Companion.uid
import ua.co.myrecipes.util.Resource

class FakeUserRepositoryTest: UserRepositoryInt {
    private val userList = mutableListOf<User>()
    private var shouldReturnError = false

    override suspend fun register(email: String, password: String): Resource<AuthResult> {
        userList.add(User(email=email))
        return Resource.Error("User is created")
    }

    override suspend fun login(email: String, password: String, token: String): Resource<AuthResult> {
        userList.find { it.email == email }!!
        return Resource.Error("User is log in")
    }


    override suspend fun getUserToken(nickName: String): String {
        return userList.find { it.nickname == nickName }?.token ?: ""
    }

    override suspend fun getUserByNickName(nickName: String): Resource<User> {
        return Resource.Success(userList.find { it.nickname == nickName }!!)
    }

    override suspend fun getCurrentUser(): Resource<User> {
        return Resource.Success(userList.find { it.uid == uid }!!)
    }


    override suspend fun updateAbout(about: String) {
        userList.find { it.uid == uid }?.about = about
    }

    override suspend fun updateImage(imgBitmap: Bitmap) {
        userList.find { it.uid == uid }?.img = imgBitmap.toString()
    }
}