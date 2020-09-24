package ua.co.myrecipes.repository.user

import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.DataState

interface UserRepositoryInt {
    suspend fun registerUser(email: String, password: String): AuthResult

    suspend fun signInUser(email: String, password: String): AuthResult

    suspend fun logOut()

    fun getUserEmail(): String

    fun getUser(userName: String): Flow<DataState<User>>

    fun getCurrentUser(): Flow<DataState<User>>

    fun updateAbout(about: String)
}