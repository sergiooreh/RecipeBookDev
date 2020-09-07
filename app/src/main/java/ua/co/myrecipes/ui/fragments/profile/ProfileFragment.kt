package ua.co.myrecipes.ui.fragments.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.User
import ua.co.myrecipes.viewmodels.UserViewModel
import ua.co.myrecipes.util.DataState

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userViewModel.getUserEmail() == ""){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, false)
                .build()
            findNavController().navigate(
                R.id.action_profileFragment_to_regFragment,
                savedInstanceState,
                navOptions
            )
        }
        getUser()

    }

    private fun getUser(){
        userViewModel.getUser().observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success<User> ->{
                    displayProgressBar(false)
                    val user = it.data
                    nickname_tv.text = user.nickname
                    recipes_tv.text = user.recipe.size.toString()
                    followers_tv.text = user.followers.size.toString()
                    following_tv.text = user.following.size.toString()
                    liked_tv.text = user.likedRecipes.size.toString()
                }
                is DataState.Error -> {
                    displayProgressBar(false)
//                    displayError(dataState.exception.message)
                }
                is DataState.Loading -> {
                    displayProgressBar(true)
                }
            }
        })
    }

    private fun displayProgressBar(isDisplayed: Boolean){
        progress_bar_profile.visibility = if(isDisplayed) View.VISIBLE else View.GONE
    }
}