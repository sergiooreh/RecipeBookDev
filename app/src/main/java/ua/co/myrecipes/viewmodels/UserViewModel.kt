package ua.co.myrecipes.viewmodels

import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.co.myrecipes.model.User
import ua.co.myrecipes.repository.user.UserRepositoryInt
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.Resource

class UserViewModel @ViewModelInject constructor(
    private val userRepository: UserRepositoryInt
): ViewModel() {

    private val _authStatus = MutableLiveData<Resource<String>>()
    val authStatus: LiveData<Resource<String>> = _authStatus

    private val _user = MutableLiveData<DataState<User>>()
    val user: LiveData<DataState<User>> = _user

    fun register(email: String, password: String, confirmPassword: String){
        _authStatus.postValue(Resource.loading(null))
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            _authStatus.postValue(Resource.error("Please fill out all the fields",null))
            return
        }
        if (password != confirmPassword){
            _authStatus.postValue(Resource.error("The passwords do not match",null))
            return
        }
        viewModelScope.launch {
            val result = userRepository.register(email, password)
            _authStatus.postValue(result)
        }
    }

    fun login(email: String, password: String, token: String){
        if (email.isEmpty() || password.isEmpty()){
            _authStatus.postValue(Resource.error("Please fill out all the fields",null))
            return
        }
        viewModelScope.launch {
            val result = userRepository.login(email, password, token)
            _authStatus.postValue(result)
        }
    }

    fun getUserEmail() = userRepository.getUserEmail()

    suspend fun getUserImgAsync() =
        withContext(viewModelScope.coroutineContext) {
            userRepository.getUserImg()
        }

    fun getUserTokenAsync(nickName: String) = viewModelScope.async {
            userRepository.getUserToken(nickName)
        }

    fun getUser(userName: String) {
        if (userName!="" && userName!=getUserEmail().substringBefore("@")){
            userRepository.getUserByName(userName).onEach { _user.value = it }.launchIn(viewModelScope)
        } else {
            userRepository.getCurrentUser().onEach { _user.value = it }.launchIn(viewModelScope)
        }
    }

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
}