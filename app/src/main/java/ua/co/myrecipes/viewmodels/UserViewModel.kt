package ua.co.myrecipes.viewmodels

import android.app.Application
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.co.myrecipes.MyApp
import ua.co.myrecipes.R
import ua.co.myrecipes.model.User
import ua.co.myrecipes.repository.user.UserRepositoryInt
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.Resource
import java.util.*

class UserViewModel @ViewModelInject constructor(
    private val userRepository: UserRepositoryInt,
    application: Application
): AndroidViewModel(application) {
    private val resources: Resources = getApplication<MyApp>().resources

    init {
        val lang = PreferenceManager.getDefaultSharedPreferences(application).getString("language", "")
        resources.configuration.setLocale(Locale(lang?:"en"))
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
    }

    private val _authStatus = MutableLiveData<Resource<String>>()
    val authStatus: LiveData<Resource<String>> = _authStatus

    private val _user = MutableLiveData<DataState<User>>()
    val user: LiveData<DataState<User>> = _user

    fun register(email: String, password: String, confirmPassword: String){
        _authStatus.postValue(Resource.loading(null))
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            _authStatus.postValue(Resource.error(resources.getString(R.string.please_fill_out_all_the_fields),null))
            return
        }
        if (password != confirmPassword){
            _authStatus.postValue(Resource.error(resources.getString(R.string.ERROR_PASSWORDS_DO_NOT_MATCH),null))
            return
        }
        viewModelScope.launch {
            val result = userRepository.register(email, password)
            result.message = when(result.message){
                "ERROR_INVALID_EMAIL" -> resources.getString(R.string.ERROR_INVALID_EMAIL)
                "ERROR_EMAIL_ALREADY_IN_USE" -> resources.getString(R.string.ERROR_EMAIL_ALREADY_IN_USE)
                "ERROR_ACTIVATION_LINK_SENT_TO_YOU" -> resources.getString(R.string.ERROR_ACTIVATION_LINK_SENT_TO_YOU)
                else -> result.message
            }
            _authStatus.postValue(result)
        }
    }

    fun login(email: String, password: String, token: String){
        if (email.isEmpty() || password.isEmpty()){
            _authStatus.postValue(Resource.error(resources.getString(R.string.please_fill_out_all_the_fields),null))
            return
        }
        viewModelScope.launch {
            val result = userRepository.login(email, password, token)
            result.message = when(result.message){
                "ERROR_USER_NOT_FOUND" -> resources.getString(R.string.ERROR_USER_NOT_FOUND)
                "EMAIL_IS_NOT_ACTIVATED" -> resources.getString(R.string.EMAIL_IS_NOT_ACTIVATED)
                "ERROR_INVALID_EMAIL" -> resources.getString(R.string.ERROR_INVALID_EMAIL)
                "ERROR_WRONG_PASSWORD" -> resources.getString(R.string.ERROR_WRONG_PASSWORD)
                "ERROR_USER_DISABLED" -> resources.getString(R.string.ERROR_USER_DISABLED)
                else -> result.message
            }
            _authStatus.postValue(result)
        }
    }

    fun getUserEmail() = userRepository.getUserEmail()

    suspend fun getUserImgAsync() = withContext(viewModelScope.coroutineContext) {
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

    fun updateImage(imgBitmap: Bitmap) = viewModelScope.launch {
        userRepository.updateImage(imgBitmap)
    }

    fun updateAbout(about: String) = viewModelScope.launch {
        userRepository.updateAbout(about)
    }
}