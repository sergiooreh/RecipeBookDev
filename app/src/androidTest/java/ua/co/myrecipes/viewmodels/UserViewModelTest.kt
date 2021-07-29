package ua.co.myrecipes.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import ua.co.myrecipes.repository.user.FakeUserRepositoryTest
import ua.co.myrecipes.util.Resource

class UserViewModelTest{
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: UserViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setup(){
        viewModel = UserViewModel(FakeUserRepositoryTest(), TestCoroutineDispatcher())
    }

    /*Register*/
    @Test
    fun registerUserWithoutPassword(){
        viewModel.register("ddd@ddd.ddd", "", "111222")
        val value = viewModel.authStatus.value
        assertThat(value).isEqualTo(UserViewModel.RegisterEvent.ErrorFieldIsEmpty)
    }

    @Test
    fun registerUserWithWrongConfirmPassword(){
        viewModel.register("ddd@ddd.ddd", "111111", "111112")
        val value = viewModel.authStatus.value
        assertThat(value).isEqualTo(UserViewModel.RegisterEvent.ErrorPasswordsNotMatch)
    }

    @Test
    fun registerUserWithRightCredentials(){
        viewModel.register("ddd@ddd.ddd", "111111", "111111")
        val value = viewModel.authStatus.value
        assertThat(value).isEqualTo(UserViewModel.RegisterEvent.Success)
    }

    /*Login*/
    @Test
    fun loginUserWithRightCredentials(){
        viewModel.login("aaa@aaa.aaa", "111111", "")
        val value = viewModel.authStatus.value
        assertThat(value).isEqualTo(UserViewModel.RegisterEvent.Success)
    }

    @Test
    fun loginUserWithWrongCredentials(){
        viewModel.login("abc@aaa.aaa", "111111", "")
        val value = viewModel.authStatus.value
        assertThat(value).isInstanceOf(UserViewModel.RegisterEvent.ErrorLogIn(Resource.Error("User is not found"))::class.java)
    }

    @Test
    fun loginUserWithoutPassword(){
        viewModel.login("aaa@aaa.aaa", "", "")
        val value = viewModel.authStatus.value
        assertThat(value).isEqualTo(UserViewModel.RegisterEvent.ErrorFieldIsEmpty)
    }

    @Test
    fun loginUserWithoutEmail(){
        viewModel.login("", "111111", "")
        val value = viewModel.authStatus.value
        assertThat(value).isEqualTo(UserViewModel.RegisterEvent.ErrorFieldIsEmpty)
    }
}