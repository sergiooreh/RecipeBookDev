package ua.co.myrecipes.ui.fragments.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.co.myrecipes.R
import ua.co.myrecipes.util.Constants.KEY_FIRST_NEW_TOKEN
import ua.co.myrecipes.viewmodels.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeRegFragment : Fragment(R.layout.fragment_welcome) {
    @set:Inject
    var isFirstAppOpen = true

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isFirstAppOpen){
            findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
        }

        tvSkip.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
        }

        tvLogIn.setOnClickListener {
            signIn()
        }

        signup_btn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeRegFragment_to_registrationFragment)
        }
    }

    private fun signIn(){
        val email = email_edt.text.toString()
        val password = password_edt.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    val token = sharedPreferences.getString(KEY_FIRST_NEW_TOKEN,"") ?: ""
                    userViewModel.signInUser(email, password, token)
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
                        activity?.recreate()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

