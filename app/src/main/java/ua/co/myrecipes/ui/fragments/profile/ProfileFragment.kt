package ua.co.myrecipes.ui.fragments.profile

import android.app.AlertDialog
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.RequestManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.options
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import ua.co.myrecipes.R
import ua.co.myrecipes.databinding.FragmentProfileBinding
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.AuthUtil
import ua.co.myrecipes.util.EventObserver
import ua.co.myrecipes.viewmodels.UserViewModel
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(){
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentProfileBinding::inflate

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

        binding.userImv.setOnClickListener {
            cropImage.launch(
                options {
                    setAspectRatio(500, 500)
                    setFixAspectRatio(true)
                }
            )
        }

        binding.profileLogOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(
                R.id.action_profileFragment_to_regFragment,
                bundleOf("redirectToRegister" to true)
            )
            activity?.recreate()
        }

        binding.linearLayout.setOnClickListener {
            if (binding.recipesTv.text=="0"){
                showToast(R.string.the_list_of_recipes_is_empty)
                return@setOnClickListener
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_recipesFragment,
                bundleOf("recipeAuthor" to userName)
            )
        }

        binding.linearLayoutLiked.setOnClickListener {
            if (binding.likedTv.text=="0"){
                showToast(textResource = R.string.the_list_of_recipes_is_empty)
                return@setOnClickListener
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_recipesFragment, bundleOf(
                    "recipeAuthor" to "@".plus(userName))
            )
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            binding.userImv.setImageURI(uri)
            userViewModel.updateImage((binding.userImv.drawable as BitmapDrawable).bitmap)
            glide.load(uri).into(binding.userImv)
            glide.load(uri).into(
                (activity?.findViewById(R.id.navView) as NavigationView)
                    .getHeaderView(0).findViewById<CircleImageView>(R.id.drawer_user_img))
        } else {
            val exception = result.error
        }
    }

    private fun subscribeToObservers(){
        userViewModel.user.observe(viewLifecycleOwner, EventObserver(
            onLoading = { displayProgressBar(binding.progressBarProfile, isDisplayed = true) },
            onError = { displayProgressBar(binding.progressBarProfile, isDisplayed = false) }
        ){ user ->
            userName = user.nickname
            settingProfileForUser()

            displayProgressBar(binding.progressBarProfile, isDisplayed = false)
            binding.nicknameTv.text = user.nickname
            binding.recipesTv.text = user.recipes.size.toString()
            binding.likedTv.text = user.likedRecipes.size.toString()
            binding.userAboutTv.text = user.about
            if (user.img != "") {
                glide.load(user.img.toUri()).into(binding.userImv)
            }
        })
    }

    private fun settingProfileForUser(){
        if (userName == AuthUtil.email.substringBefore("@")){
            activity?.title = getString(R.string.profile)
            binding.constraintLayout2.setOnClickListener {
                actNumberDialog()
            }
        } else {
            activity?.title = userName
            binding.aboutMeImg.visibility = View.GONE
            binding.profileLogOutBtn.visibility = View.GONE
            binding.imageButton.visibility = View.GONE
            binding.linearLayoutLiked.visibility = View.GONE
        }
    }

    private fun actNumberDialog() {
        val editText = EditText(requireContext()).apply {
            setText(binding.userAboutTv.text.trim())
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
                binding.userAboutTv.text = editText.text.toString()}
            setNegativeButton(R.string.CANCEL) { dialogInterface, _ -> dialogInterface.cancel() }
            show()
        }
    }
}