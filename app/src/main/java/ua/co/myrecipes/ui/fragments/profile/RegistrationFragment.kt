package ua.co.myrecipes.ui.fragments.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.co.myrecipes.R
import ua.co.myrecipes.viewmodels.UserViewModel

@AndroidEntryPoint
class RegistrationFragment : Fragment(R.layout.fragment_registration) {
    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createAccount_btn.setOnClickListener {
            registration()
        }
    }

    private fun registration(){
        val email = email_edt.text.toString()
        val password = password_edt.text.toString()
        val confirmPassword = confirm_password_edt.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword){
                lifecycleScope.launch {
                    try {
                        userViewModel.registerUser(email, password)
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(R.id.action_registrationFragment_to_homeFragment)
                            activity?.recreate()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else Toast.makeText(context,"Your passwords do not match",Toast.LENGTH_LONG).show()
        }
    }

}