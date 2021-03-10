package ua.co.myrecipes.viewmodels

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Test
import ua.co.myrecipes.repository.user.FakeUserRepositoryTest

class UserViewModelTest{

    private lateinit var viewModel: UserViewModel

    @Before
    fun setup(){
        viewModel = UserViewModel(FakeUserRepositoryTest(), ApplicationProvider.getApplicationContext(), Dispatchers.IO)
    }

    @Test
    fun ddd(){

    }
}