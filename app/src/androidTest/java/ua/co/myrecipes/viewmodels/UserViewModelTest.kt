package ua.co.myrecipes.viewmodels

import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Test
import ua.co.myrecipes.MyApp
import ua.co.myrecipes.getOrAwaitValueTest
import ua.co.myrecipes.repository.user.FakeUserRepositoryTest
import ua.co.myrecipes.util.Resource

class UserViewModelTest{

    private lateinit var viewModel: UserViewModel

    @Before
    fun setup(){
        viewModel = UserViewModel(FakeUserRepositoryTest(), Dispatchers.IO)
    }

    @Test
    fun registerUser(){
        viewModel.register("aaa@aaa.aaa", "", "")

        val value = viewModel.authStatus.getOrAwaitValueTest()

        assertThat(value.getContentIfNotHandled()).isEqualTo(Resource.Error::class.java)
    }
}