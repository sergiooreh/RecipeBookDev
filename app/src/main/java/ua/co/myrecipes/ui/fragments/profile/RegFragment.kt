package ua.co.myrecipes.ui.fragments.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.coroutines.*
import ua.co.myrecipes.R
import ua.co.myrecipes.viewmodels.UserViewModel

@AndroidEntryPoint
class RegFragment: Fragment(R.layout.fragment_welcome) {
    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintLayout.visibility = View.GONE
        logIn_btn.visibility = View.VISIBLE

        signup_btn.setOnClickListener {
            findNavController().navigate(R.id.action_regFragment_to_registrationFragment)
        }

        logIn_btn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn(){
        val email = email_edt.text.toString()
        val password = password_edt.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    userViewModel.signInUser(email, password)
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_regFragment_to_profileFragment)
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