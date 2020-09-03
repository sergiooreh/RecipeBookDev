package ua.co.myrecipes.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.launch
import ua.co.myrecipes.repository.user.UserRepositoryInt

class UserViewModel @ViewModelInject constructor(
    private val userRepository: UserRepositoryInt
): ViewModel() {
    suspend fun registerUser(email: String, password: String): AuthResult = userRepository.registerUser(email, password)

    suspend fun signInUser(email: String, password: String): AuthResult = userRepository.signInUser(email, password)

    fun getUserEmail() = userRepository.getUserEmail()

    fun getUser() = userRepository.getUser().asLiveData()

    fun logOut() = viewModelScope.launch {
        userRepository.logOut()
    }


}