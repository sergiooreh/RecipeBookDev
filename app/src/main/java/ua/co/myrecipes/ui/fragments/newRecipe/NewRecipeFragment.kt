package ua.co.myrecipes.ui.fragments.newRecipe

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_recipe.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.Constants
import ua.co.myrecipes.util.Constants.REQUEST_CODE
import ua.co.myrecipes.util.Permissions
import ua.co.myrecipes.util.RecipeType
import ua.co.myrecipes.viewmodels.UserViewModel

@AndroidEntryPoint
class NewRecipeFragment : Fragment(R.layout.fragment_new_recipe),EasyPermissions. PermissionCallbacks {
    private val userViewModel: UserViewModel by viewModels()
    private var imgUri: Uri? = null
    var time = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add_recipe_img.setOnClickListener {
            if (!Permissions.hasStoragePermissions(requireContext())){
                requestPermissions()
                return@setOnClickListener
            }
            openGalleryForImage()
        }

        prep_time_btn.setOnClickListener {
            val timePickerDialog = TimePickerDialog(requireContext(), android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                { timePicker, h, m ->
                    time = if (m==0) "$h Hr" else "$h Hr $m Min"
                    new_time_tv.apply {
                        visibility = View.VISIBLE
                        text = time
                    }
                }, 0, 0, true
            )
            timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            timePickerDialog.show()
        }

        type_spinner.adapter =
            ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, RecipeType.values())

        to_ingredients_fab.setOnClickListener {
            if (!validateInput(recipe_name_et.text.toString(), recipe_name_til)){
                return@setOnClickListener
            }

            if (new_time_tv.visibility == View.GONE){
                Snackbar.make(it,"Choose time", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (recipe_img.drawable == null){
                Snackbar.make(it,"Insert image", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val recipe = Recipe().apply {
                name = recipe_name_et.text.toString().trim()
                author = userViewModel.getUserEmail().substringBefore("@")
                durationPrepare = time
                type = type_spinner.selectedItem as RecipeType
                img = imgUri.toString()
                imgBitmap = (recipe_img.drawable as BitmapDrawable).bitmap
            }

            findNavController().navigate(
                R.id.action_newRecipeFragment_to_newRecipeIngrFragment, bundleOf(
                    "recipe" to recipe
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = "Add new recipe"
    }

    private fun validateInput(string: String, textInputLayout: TextInputLayout)=
        if (string.isEmpty()){
            textInputLayout.error = "The field can't be empty"
            false
        } else {
            textInputLayout.error = null
            true
        }

    private fun openGalleryForImage() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(this, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE ->{
                if (resultCode == Activity.RESULT_OK){ data?.data?.let { launchImageCrop(it) } } }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ->{
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK){
                    result.uri?.let {
                        imgUri = it
                        recipe_img.setImageURI(it)
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
            "You have to accept permission to load image",
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