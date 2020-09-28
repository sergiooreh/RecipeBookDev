package ua.co.myrecipes.ui.fragments.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.android.synthetic.main.fragment_new_recipe.*
import kotlinx.android.synthetic.main.fragment_profile.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import ua.co.myrecipes.R
import ua.co.myrecipes.model.User
import ua.co.myrecipes.util.Constants
import ua.co.myrecipes.viewmodels.UserViewModel
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.Permissions
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile), EasyPermissions. PermissionCallbacks {
    private val userViewModel: UserViewModel by viewModels()
    private var userName: String = ""

    @Inject
    lateinit var glide: RequestManager

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
            if (recipes_tv.text=="0"){
                Toast.makeText(requireContext(),R.string.you_dont_have_your_own_recipes,Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_profileFragment_to_recipesFragment, bundleOf("recipeAuthor" to userName))
        }

        linearLayoutLiked.setOnClickListener {
            if (liked_tv.text=="0"){
                Toast.makeText(requireContext(),R.string.you_dont_have_liked_recipes,Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_profileFragment_to_recipesFragment, bundleOf("recipeAuthor" to "@".plus(userName)))
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.profile)
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
                user_imv.setOnClickListener {
                    if (!Permissions.hasStoragePermissions(requireContext())){
                        requestPermissions()
                        return@setOnClickListener
                    }
                    openGalleryForImage()
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
                if(user.img != ""){
                    glide.load(user.img.toUri()).into(user_imv)
                }
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
            setTitle(R.string.about_you)
            setView(editText)
            setPositiveButton(R.string.ok) { _, _ ->
                userViewModel.updateAbout(editText.text.toString())
                userAbout_tv.text = editText.text.toString()}
            setNegativeButton(R.string.CANCEL) { dialogInterface, _ -> dialogInterface.cancel() }
            show()
        }
    }

    private fun openGalleryForImage() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(this, Constants.REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            Constants.REQUEST_CODE ->{
                if (resultCode == Activity.RESULT_OK){ data?.data?.let { launchImageCrop(it) } } }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ->{
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK){
                    result.uri?.let {
                        user_imv.setImageURI(it)
                        userViewModel.updateImage((user_imv.drawable as BitmapDrawable).bitmap)
                        glide.load(it).into(user_imv)
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    /////////
                }
            }
        }
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setAspectRatio(500,500)
            .setFixAspectRatio(true)
            .start(requireContext(), this)
    }

    private fun requestPermissions(){
        if (Permissions.hasStoragePermissions(requireContext())){ return }
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.you_have_to_accept_permission_to_load_image),
            Constants.REQUEST_CODE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) { openGalleryForImage() }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        } else{
            requestPermissions()
            return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}