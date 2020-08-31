package ua.co.myrecipes.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.launch
import ua.co.myrecipes.repository.UserRepository

class UserViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    suspend fun registerUser(email: String, password: String): AuthResult = userRepository.registerUser(email, password)


    suspend fun signInUser(email: String, password: String): AuthResult = userRepository.signInUser(email, password)


}