package ua.co.myrecipes.ui.fragments.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.co.myrecipes.R
import ua.co.myrecipes.ui.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationFragment : Fragment(R.layout.fragment_registration) {
    @set:Inject
    var isFirstAppOpen = true

    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isFirstAppOpen){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.registrationFragment, true)           //if not first time -> setupFragment take off from fragmentStack
                .build()
            findNavController().navigate(
                R.id.action_registrationFragment_to_homeFragment,
                savedInstanceState,
                navOptions
            )
        }
        activity?.drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        tvSkip.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.registrationFragment, true)           //if not first time -> setupFragment take off from fragmentStack
                .build()
            findNavController().navigate(
                R.id.action_registrationFragment_to_homeFragment,
                savedInstanceState,
                navOptions
            )
        }

        tvLogIn.setOnClickListener {
            signInUp(false)
        }

        signup_btn.setOnClickListener {
            signInUp(true)
        }
    }


    /*private fun nickNameDialog(){
        val editText = EditText(requireContext())
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle("Your Nickname")
            setView(editText)
            setPositiveButton("Save") { _, _ -> }
            setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.cancel() }
            create()
        }

        val alert = builder.show()
        val button = alert.getButton(AlertDialog.BUTTON_POSITIVE)
        button.setOnClickListener {
            if (editText.text.toString().trim().isNotEmpty()) {
                nickName = editText.text.toString()
                alert.dismiss()
            }
        }
    }*/

    private fun signInUp(registration: Boolean){
        val email = email_edt.text.toString()
        val password = password_edt.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (registration) {
                        userViewModel.registerUser(email, password)
                    } else {
                        userViewModel.signInUser(email, password)
                    }
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_registrationFragment_to_homeFragment)
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

