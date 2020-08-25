package ua.co.myrecipes.ui.fragments.newRecipe

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_new_recipe.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.Constants
import ua.co.myrecipes.util.Constants.REQUEST_CODE
import ua.co.myrecipes.util.Permissions
import ua.co.myrecipes.util.RecipeType

class NewRecipeFragment : Fragment(R.layout.fragment_new_recipe),EasyPermissions. PermissionCallbacks {
    private var img: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add_recipe_img.setOnClickListener {
            if (!Permissions.hasStoragePermissions(requireContext())){
                requestPermissions()
                return@setOnClickListener
            }
            openGalleryForImage()
        }

        type_spinner.adapter =
            ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, RecipeType.values())

        to_ingredients_fab.setOnClickListener {
            if (!validateInput(recipe_name_et.text.toString(), recipe_name_til) ||
                (!validateInput(prep_time_et.text.toString(), prep_time_til))){
                return@setOnClickListener
            }

            if (recipe_img.drawable == null){
                Snackbar.make(it,"Insert image", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val recipe = Recipe().apply {
                name = recipe_name_et.text.toString().trim()
                durationPrepare = prep_time_et.text.toString().trim().toInt()
                type = type_spinner.selectedItem as RecipeType
                img = (recipe_img.drawable as BitmapDrawable).bitmap
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
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            img = data?.data!!
            recipe_img.setImageURI(img)
        }
    }

    private fun requestPermissions(){
        if (Permissions.hasStoragePermissions(requireContext())){
            return
        }
        EasyPermissions.requestPermissions(
            this,
            "You have to accept permission to load image",
            Constants.REQUEST_CODE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        openGalleryForImage()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        } else{
            requestPermissions()
            return
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}