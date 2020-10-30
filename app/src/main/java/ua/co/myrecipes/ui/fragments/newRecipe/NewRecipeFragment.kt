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
import androidx.navigation.NavOptions
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
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.Constants
import ua.co.myrecipes.util.Constants.REQUEST_CODE
import ua.co.myrecipes.util.Permissions
import ua.co.myrecipes.util.RecipeType
import ua.co.myrecipes.viewmodels.UserViewModel

@AndroidEntryPoint
class NewRecipeFragment : BaseFragment(R.layout.fragment_new_recipe) {
    private val userViewModel: UserViewModel by viewModels()
    private var imgUri: Uri? = null
    private var time = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userViewModel.getUserEmail().isBlank()){
            findNavController().navigate(R.id.action_newRecipeFragment_to_regFragment, bundleOf("redirectToRegister" to true))
        }

        add_recipe_img.setOnClickListener {
            if (!Permissions.hasStoragePermissions(requireContext())){
                requestPermissions()
                return@setOnClickListener
            }
            openGalleryForImage()
        }

        prep_time_btn.setOnClickListener {
            choosingTime()
        }

        type_spinner.adapter =
            ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.recipeTypes))

        to_ingredients_fab.setOnClickListener {
            if (!validateInput(recipe_name_et.text.toString(), recipe_name_til)){
                return@setOnClickListener
            }

            if (new_time_tv.text.isBlank()){
                showSnackBar(R.string.choose_time)
                return@setOnClickListener
            }

            if (recipe_img.drawable == null){
                showSnackBar(R.string.insert_image)
                return@setOnClickListener
            }

            val recipe = Recipe().apply {
                name = recipe_name_et.text.toString().trim()
                author = userViewModel.getUserEmail().substringBefore("@")
                durationPrepare = time
                type = RecipeType.values()[type_spinner.selectedItemPosition]
                imgUrl = imgUri.toString()
                imgBitmap = (recipe_img.drawable as BitmapDrawable).bitmap
            }
            findNavController().navigate(R.id.action_newRecipeFragment_to_newRecipeIngrFragment, bundleOf("recipe" to recipe))
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.add_new_recipe)
    }

    private fun choosingTime(){
        val timePickerDialog = TimePickerDialog(requireContext(), android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
            { _, h, m ->
                time = when {
                    h==0 -> "$m ${getString(R.string.min)}"
                    m==0 -> "$h ${getString(R.string.hour)}"
                    else -> "$h ${getString(R.string.hour)}  $m ${getString(R.string.min)}"
                }
                new_time_tv.text = time
            }, 0, 0, true
        )
        timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.show()
    }

    private fun validateInput(string: String, textInputLayout: TextInputLayout)=
        if (string.isEmpty()){
            textInputLayout.error = getString(R.string.the_field_cant_be_empty)
            false
        } else {
            textInputLayout.error = null
            true
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
                        setImage_tv.hint = ""                           //clear textView
                    }
                }
            }
        }
    }
}