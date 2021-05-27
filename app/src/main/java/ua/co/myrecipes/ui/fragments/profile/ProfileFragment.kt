package ua.co.myrecipes.ui.fragments.profile

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.android.synthetic.main.fragment_new_recipe.*
import kotlinx.android.synthetic.main.fragment_profile.*
import ua.co.myrecipes.R
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.AuthUtil
import ua.co.myrecipes.util.EventObserver
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
        if (AuthUtil.email.isBlank()){
            findNavController().navigate(
                R.id.action_profileFragment_to_regFragment,
                bundleOf("redirectToRegister" to true)
            )
        }
        userViewModel.getUser(userName)
        subscribeToObservers()

        profile_log_out_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
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
                    "recipeAuthor" to "@".plus(userName))
            )
        }

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let {
                user_imv.setImageURI(it)
                userViewModel.updateImage((user_imv.drawable as BitmapDrawable).bitmap)
                glide.load(it).into(user_imv)
                glide.load(it).into(
                    (activity?.findViewById(R.id.navView) as NavigationView)
                        .getHeaderView(0).drawer_user_img)
            }
        }
    }

    private fun subscribeToObservers(){
        userViewModel.user.observe(viewLifecycleOwner, EventObserver(
            onLoading = { displayProgressBar(progress_bar_profile, isDisplayed = true) },
            onError = { displayProgressBar(progress_bar_profile, isDisplayed = false) }
        ){ user ->
            userName = user.nickname
            settingProfileForUser()

            displayProgressBar(progress_bar_profile, isDisplayed = false)
            nickname_tv.text = user.nickname
            recipes_tv.text = user.recipes.size.toString()
            liked_tv.text = user.likedRecipes.size.toString()
            userAbout_tv.text = user.about
            if (user.img != "") {
                glide.load(user.img.toUri()).into(user_imv)
            }
        })
    }

    private fun settingProfileForUser(){
        if (userName == AuthUtil.email.substringBefore("@")){
            activity?.title = getString(R.string.profile)
            constraintLayout2.setOnClickListener {
                actNumberDialog()
            }
            user_imv.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    return@setOnClickListener
                }
                cropActivityResultLauncher.launch(null)
            }
        } else {
            activity?.title = userName
            aboutMe_img.visibility = View.GONE
            profile_log_out_btn.visibility = View.GONE
            imageButton.visibility = View.GONE
            linearLayoutLiked.visibility = View.GONE
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
}