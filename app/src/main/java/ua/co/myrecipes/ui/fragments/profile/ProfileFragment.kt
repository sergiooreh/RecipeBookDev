package ua.co.myrecipes.ui.fragments.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.User
import ua.co.myrecipes.viewmodels.UserViewModel
import ua.co.myrecipes.util.DataState

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val userViewModel: UserViewModel by viewModels()
    private var userName: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = arguments?.getString("userName")?: ""
        if (userViewModel.getUserEmail() == ""){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, false)
                .build()
            findNavController().navigate(
                R.id.action_profileFragment_to_regFragment,
                savedInstanceState,
                navOptions)
        }
        getUser()

        log_out_btn2.setOnClickListener {
            userViewModel.logOut()
            val navOptions = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .build()
            findNavController().navigate(R.id.regFragment,savedInstanceState,navOptions)
            activity?.recreate()
        }

        userRecipes_lnr.setOnClickListener {
            if (recipes_tv.toString()=="0"){
                Toast.makeText(requireContext(),"You don't have your own recipes",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_profileFragment_to_recipesFragment, bundleOf("recipeAuthor" to userName))
        }

        linearLayoutLiked.setOnClickListener {
            if (liked_tv.text=="0"){
                Toast.makeText(requireContext(),"You don't have liked recipes",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_profileFragment_to_recipesFragment, bundleOf("recipeAuthor" to "@".plus(userName)))
        }
    }

    private fun getUser(){
        if (userName!="" && userName!=userViewModel.getUserEmail().substringBefore("@")){
            userViewModel.getUser(userName).observe(viewLifecycleOwner, {
                aboutMe_img.visibility = View.GONE
                log_out_btn2.visibility = View.GONE
                handlingStates(it)
            })
        } else{
            userViewModel.getCurrentUser().observe(viewLifecycleOwner, {
                handlingStates(it)
                userName = userViewModel.getUserEmail().substringBefore("@")
                constraintLayout2.setOnClickListener {
                    actNumberDialog()
                }
            })
        }

    }

    private fun displayProgressBar(isDisplayed: Boolean){
        progress_bar_profile.visibility = if(isDisplayed) View.VISIBLE else View.GONE
    }

    private fun handlingStates(it: DataState<User>){
        when(it){
            is DataState.Success<User> ->{
                displayProgressBar(false)
                val user = it.data
                nickname_tv.text = user.nickname
                recipes_tv.text = user.recipe.size.toString()
                liked_tv.text = user.likedRecipes.size.toString()
                userAbout_tv.text = user.about
            }
            is DataState.Error -> {
                displayProgressBar(false)
//                    displayError(dataState.exception.message)
            }
            is DataState.Loading -> {
                displayProgressBar(true)
            }
        }
    }

    private fun actNumberDialog() {
        val editText = EditText(requireContext())
        editText.setText(userAbout_tv.text)
        AlertDialog.Builder(requireContext()).apply {
            setTitle("About You")
            setView(editText)
            setPositiveButton("OK") { _, _ ->
                userViewModel.updateAbout(editText.text.toString())
                userAbout_tv.text = editText.text.toString()}
            setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.cancel() }
            show()
        }
    }
}