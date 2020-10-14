package ua.co.myrecipes.viewmodels

import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.model.User
import ua.co.myrecipes.repository.user.UserRepositoryInt

class UserViewModel @ViewModelInject constructor(
    private val userRepository: UserRepositoryInt
): ViewModel() {
    suspend fun registerUser(email: String, password: String): AuthResult = userRepository.registerUser(email, password)

    suspend fun signInUser(email: String, password: String, token: String): AuthResult = userRepository.signInUser(email, password, token)

    fun getUserEmail() = userRepository.getUserEmail()

    fun getUserImg() = viewModelScope.async {
        userRepository.getUserImg()
    }

    fun getUserToken(nickName: String) = viewModelScope.async {
            userRepository.getUserToken(nickName)
        }


    fun getCurrentUser() = userRepository.getCurrentUser().asLiveData()

    fun getUser(userName: String) = userRepository.getUser(userName).asLiveData()

    fun logOut() = viewModelScope.launch {
        userRepository.logOut()
    }

    fun updateImage(imgBitmap: Bitmap){
        viewModelScope.launch {
            userRepository.updateImage(imgBitmap)
        }
    }

    fun updateAbout(about: String){
        viewModelScope.launch {
            userRepository.updateAbout(about)
        }
    }

    fun updateToken(token:String){
        viewModelScope.launch {
            userRepository.updateToken(token)
        }
    }
}