package ua.co.myrecipes.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ua.co.myrecipes.model.User
import ua.co.myrecipes.repository.user.UserRepositoryInt
import ua.co.myrecipes.util.AuthUtil
import ua.co.myrecipes.util.Event
import ua.co.myrecipes.util.Resource
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepositoryInt,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    private val _authStatus = MutableLiveData<RegisterEvent>()
    val authStatus: LiveData<RegisterEvent> = _authStatus

    private val _user = MutableLiveData<Event<Resource<User>>>()
    val user: LiveData<Event<Resource<User>>> = _user

    fun register(email: String, password: String, confirmPassword: String){
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            _authStatus.postValue(RegisterEvent.ErrorFieldIsEmpty)
            return
        }
        if (password != confirmPassword){
            _authStatus.postValue(RegisterEvent.ErrorPasswordsNotMatch)
            return
        }
        viewModelScope.launch(dispatcher) {
            val result = userRepository.register(email, password)

            if (result is Resource.Success) _authStatus.postValue(RegisterEvent.Success)
            else _authStatus.postValue(RegisterEvent.ErrorLogIn(result))
        }
    }

    fun login(email: String, password: String, token: String){
        if (email.isEmpty() || password.isEmpty()){
            _authStatus.postValue(RegisterEvent.ErrorFieldIsEmpty)
            return
        }
        viewModelScope.launch(dispatcher) {
            val result = userRepository.login(email, password, token)

            if (result is Resource.Success) _authStatus.postValue(RegisterEvent.Success)
            else _authStatus.postValue(RegisterEvent.ErrorLogIn(result))
        }
    }

    fun getUserTokenAsync(nickName: String) = viewModelScope.async {
            userRepository.getUserToken(nickName)
        }

    fun getUser(nickName: String = AuthUtil.email) = viewModelScope.launch {
        _user.postValue(Event(Resource.Loading()))
        if (nickName!="" && nickName!=AuthUtil.email.substringBefore("@")){
            _user.postValue(Event(userRepository.getUserByNickName(nickName)))
        } else {
            _user.postValue(Event(userRepository.getCurrentUser()))
        }
    }

    fun updateImage(imgBitmap: Bitmap) = viewModelScope.launch {
        userRepository.updateImage(imgBitmap)
        getUser()
    }

    fun updateAbout(about: String) = viewModelScope.launch {
        userRepository.updateAbout(about)
    }

    sealed class RegisterEvent{
        object ErrorInputTooShort : RegisterEvent()
        object ErrorFieldIsEmpty : RegisterEvent()
        object ErrorPasswordsNotMatch : RegisterEvent()
        data class ErrorLogIn(val result: Resource<AuthResult>) : RegisterEvent()
        object Success : RegisterEvent()
    }
}