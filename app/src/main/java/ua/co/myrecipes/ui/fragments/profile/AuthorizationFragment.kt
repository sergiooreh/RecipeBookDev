package ua.co.myrecipes.ui.fragments.profile

import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.AndroidEntryPoint
import ua.co.myrecipes.BuildConfig
import ua.co.myrecipes.R
import ua.co.myrecipes.databinding.FragmentAuthBinding
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.Constants.KEY_FIRST_NEW_TOKEN
import ua.co.myrecipes.util.EventObserver
import ua.co.myrecipes.util.Resource
import ua.co.myrecipes.viewmodels.UserViewModel
import ua.co.myrecipes.viewmodels.UserViewModel.*
import javax.inject.Inject

@AndroidEntryPoint
class AuthorizationFragment: BaseFragment<FragmentAuthBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentAuthBinding::inflate

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        if (BuildConfig.DEBUG) {
            startDialog()
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etRegisterEmail.text.toString().trim()
            val password = binding.etRegisterPassword.text.toString().trim()
            val confirmPassword = binding.etRegisterPasswordConfirm.text.toString().trim()
            userViewModel.register(email, password, confirmPassword)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()
            val token = sharedPreferences.getString(KEY_FIRST_NEW_TOKEN, "") ?: ""
            userViewModel.login(email, password, token)
        }
    }

    private fun subscribeToObservers() {
        userViewModel.authStatus.observe(viewLifecycleOwner, { event ->
            when (event) {
                is RegisterEvent.ErrorFieldIsEmpty -> {
                    showSnackBar(R.string.please_fill_out_all_the_fields)
                }
                is RegisterEvent.ErrorPasswordsNotMatch -> {
                    showSnackBar(R.string.ERROR_PASSWORDS_DO_NOT_MATCH)
                }
                is RegisterEvent.ErrorLogIn -> {
                    when(event.result.message){
                        //register
                        "ERROR_INVALID_EMAIL" -> showSnackBar(R.string.ERROR_INVALID_EMAIL)
                        "ERROR_EMAIL_ALREADY_IN_USE" -> showSnackBar(R.string.ERROR_EMAIL_ALREADY_IN_USE)
                        "ERROR_ACTIVATION_LINK_SENT_TO_YOU" -> {
                            showSnackBar(R.string.ERROR_ACTIVATION_LINK_SENT_TO_YOU)
                            requireView().findViewById<MotionLayout>(R.id.motionLayout).transitionToStart()
                        }

                        //login
                        "ERROR_USER_NOT_FOUND" -> showSnackBar(R.string.ERROR_USER_NOT_FOUND)
                        "EMAIL_IS_NOT_ACTIVATED" -> showSnackBar(R.string.EMAIL_IS_NOT_ACTIVATED)
                        "ERROR_WRONG_PASSWORD" -> showSnackBar(R.string.ERROR_WRONG_PASSWORD)
                        "ERROR_USER_DISABLED" -> showSnackBar(R.string.ERROR_USER_DISABLED)
                        else -> showSnackBar(text = event.result.message ?: return@observe)
                    }
                }
                is RegisterEvent.Success -> {
                    findNavController().navigate(R.id.action_regFragment_to_homeFragment)
                    activity?.recreate()
                }
                else -> Unit
            }
        })
    }

    private fun startDialog() {
        val map = mapOf("serhiooreh@gmail.com" to "111111")

        val dialog = Dialog(context ?: return)
        val linearLayout = LinearLayout(context)
        linearLayout.setBackgroundColor(Color.GRAY)
        linearLayout.orientation = LinearLayout.VERTICAL

        for (data in map) {
            val textView = TextView(context)
            textView.text = data.key
            textView.setPadding(20, 20, 20, 20)
            textView.setOnClickListener {
                binding.etLoginEmail.setText(data.key)
                binding.etLoginPassword.setText(data.value)
                binding.btnLogin.performClick()
                dialog.hide()
            }
            linearLayout.addView(textView)
        }
        dialog.setContentView(linearLayout)
        dialog.show()
    }
}