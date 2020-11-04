package ua.co.myrecipes.ui.fragments.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.User
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.Constants
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.Permissions
import ua.co.myrecipes.viewmodels.UserViewModel
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile){
    private val userViewModel: UserViewModel by viewModels()
    private var userName: String = ""

    @Inject
    lateinit var glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = arguments?.getString("userName")?: ""
        if (userViewModel.getUserEmail().isBlank()){
            findNavController().navigate(
                R.id.action_profileFragment_to_regFragment,
                bundleOf("redirectToRegister" to true)
            )
        }
        userViewModel.getUser(userName)
        subscribeToObservers()

        profile_log_out_btn.setOnClickListener {
            userViewModel.logOut()
            findNavController().navigate(
                R.id.action_profileFragment_to_regFragment,
                bundleOf("redirectToRegister" to true)
            )
            activity?.recreate()
        }

        linearLayout.setOnClickListener {
            if (recipes_tv.text=="0"){
                showToast(R.string.the_list_of_recipes_is_empty)
                return@setOnClickListener
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_recipesFragment,
                bundleOf("recipeAuthor" to userName)
            )
        }

        linearLayoutLiked.setOnClickListener {
            if (liked_tv.text=="0"){
                showToast(textResource = R.string.the_list_of_recipes_is_empty)
                return@setOnClickListener
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_recipesFragment, bundleOf(
                    "recipeAuthor" to "@".plus(
                        userName
                    )
                )
            )
        }
    }

    private fun subscribeToObservers(){
        userViewModel.user.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Success<User> -> {
                    userName = it.data.nickname
                    settingProfileForUser()

                    displayProgressBar(progress_bar_profile)
                    val user = it.data
                    nickname_tv.text = user.nickname
                    recipes_tv.text = user.recipe.size.toString()
                    liked_tv.text = user.likedRecipes.size.toString()
                    userAbout_tv.text = user.about
                    if (user.img != "") {
                        glide.load(user.img.toUri()).into(user_imv)
                    }
                }
                is DataState.Error -> {
                    displayProgressBar(progress_bar_profile)
                }
                is DataState.Loading -> {
                    displayProgressBar(progress_bar_profile, isDisplayed = true)
                }
            }
        })
    }

    private fun settingProfileForUser(){
        if (userName == userViewModel.getUserEmail().substringBefore("@")){
            activity?.title = getString(R.string.profile)
            constraintLayout2.setOnClickListener {
                actNumberDialog()
            }
            user_imv.setOnClickListener {
                if (!Permissions.hasStoragePermissions(requireContext())){
                    requestPermissions()
                    return@setOnClickListener
                }
                openGalleryForImage()
            }
        } else {
            activity?.title = userName
            aboutMe_img.visibility = View.GONE
            profile_log_out_btn.visibility = View.GONE
            imageButton.visibility = View.GONE
        }
    }

    private fun actNumberDialog() {
        val editText = EditText(requireContext()).apply {
            setText(userAbout_tv.text.trim())
            isSingleLine = false
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            gravity = Gravity.START or Gravity.TOP
            setSelection(text.length)                                           //cursor at the end
            isHorizontalScrollBarEnabled = true
            filters = arrayOf<InputFilter>(LengthFilter(300))                //max length
        }

        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.about_me)
            setView(editText)
            setPositiveButton(R.string.ok) { _, _ ->
                userViewModel.updateAbout(editText.text.toString())
                userAbout_tv.text = editText.text.toString()}
            setNegativeButton(R.string.CANCEL) { dialogInterface, _ -> dialogInterface.cancel() }
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            Constants.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { launchImageCrop(it) }
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    result.uri?.let {
                        user_imv.setImageURI(it)
                        userViewModel.updateImage((user_imv.drawable as BitmapDrawable).bitmap)
                        glide.load(it).into(user_imv)
                    }
                }
            }
        }
    }
}