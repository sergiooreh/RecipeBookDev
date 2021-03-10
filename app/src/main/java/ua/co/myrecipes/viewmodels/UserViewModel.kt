package ua.co.myrecipes.viewmodels

import android.app.Application
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ua.co.myrecipes.MyApp
import ua.co.myrecipes.R
import ua.co.myrecipes.model.User
import ua.co.myrecipes.repository.user.UserRepositoryInt
import ua.co.myrecipes.util.AuthUtil
import ua.co.myrecipes.util.Event
import ua.co.myrecipes.util.Resource

class UserViewModel @ViewModelInject constructor(
    private val userRepository: UserRepositoryInt,
    application: Application,
//    @ApplicationContext val context: ApplicationContext,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): AndroidViewModel(application) {
    private val resources: Resources = getApplication<MyApp>().resources

    private val _authStatus = MutableLiveData<Event<Resource<AuthResult>>>()
    val authStatus: LiveData<Event<Resource<AuthResult>>> = _authStatus

    private val _user = MutableLiveData<Event<Resource<User>>>()
    val user: LiveData<Event<Resource<User>>> = _user

    fun register(email: String, password: String, confirmPassword: String){
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            _authStatus.postValue(Event(Resource.Error(resources.getString(R.string.please_fill_out_all_the_fields),null)))
            return
        }
        if (password != confirmPassword){
            _authStatus.postValue(Event(Resource.Error(resources.getString(R.string.ERROR_PASSWORDS_DO_NOT_MATCH),null)))
            return
        }
        viewModelScope.launch(dispatcher) {
            val result = userRepository.register(email, password)
            result.message = when(result.message){
                "ERROR_INVALID_EMAIL" -> resources.getString(R.string.ERROR_INVALID_EMAIL)
                "ERROR_EMAIL_ALREADY_IN_USE" -> resources.getString(R.string.ERROR_EMAIL_ALREADY_IN_USE)
                "ERROR_ACTIVATION_LINK_SENT_TO_YOU" -> resources.getString(R.string.ERROR_ACTIVATION_LINK_SENT_TO_YOU)
                else -> result.message
            }
            _authStatus.postValue(Event(result))
        }
    }

    fun login(email: String, password: String, token: String){
        if (email.isEmpty() || password.isEmpty()){
            _authStatus.postValue(Event(Resource.Error(resources.getString(R.string.please_fill_out_all_the_fields),null)))
            return
        }
        viewModelScope.launch(dispatcher) {
            val result = userRepository.login(email, password, token)
            result.message = when(result.message){
                "ERROR_USER_NOT_FOUND" -> resources.getString(R.string.ERROR_USER_NOT_FOUND)
                "EMAIL_IS_NOT_ACTIVATED" -> resources.getString(R.string.EMAIL_IS_NOT_ACTIVATED)
                "ERROR_INVALID_EMAIL" -> resources.getString(R.string.ERROR_INVALID_EMAIL)
                "ERROR_WRONG_PASSWORD" -> resources.getString(R.string.ERROR_WRONG_PASSWORD)
                "ERROR_USER_DISABLED" -> resources.getString(R.string.ERROR_USER_DISABLED)
                else -> result.message
            }
            _authStatus.postValue(Event(result))
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
}