package ua.co.myrecipes.ui.fragments.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.*
import ua.co.myrecipes.R
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.Constants.KEY_FIRST_NEW_TOKEN
import ua.co.myrecipes.util.Status
import ua.co.myrecipes.viewmodels.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AuthorizationFragment: BaseFragment(R.layout.fragment_auth) {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        btnRegister.setOnClickListener {
            register()
        }

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun subscribeToObservers() {
        userViewModel.authStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        showSnackBar(text = "Successfully logged in")                               //TODO
                        findNavController().navigate(R.id.action_regFragment_to_homeFragment)
                        activity?.recreate()
                    }
                    Status.ERROR -> {
                        if (result.message == "An activation link has been sent to your E-Mail. Please click it to activate your account"){
                            requireView().findViewById<MotionLayout>(R.id.motionLayout).transitionToStart()
                        }
                        showSnackBar(text = result.message ?: getString(R.string.an_unknown_error_occurred))
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
    }

    private fun login(){
        val email = etLoginEmail.text.toString().trim()
        val password = etLoginPassword.text.toString().trim()
        val token = sharedPreferences.getString(KEY_FIRST_NEW_TOKEN, "") ?: ""
        userViewModel.login(email, password, token)
    }

    private fun register(){
        val email = etRegisterEmail.text.toString().trim()
        val password = etRegisterPassword.text.toString().trim()
        val confirmPassword = etRegisterPasswordConfirm.text.toString().trim()
        userViewModel.register(email, password, confirmPassword)
    }
}