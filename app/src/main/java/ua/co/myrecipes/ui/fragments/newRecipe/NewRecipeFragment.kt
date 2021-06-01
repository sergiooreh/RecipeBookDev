package ua.co.myrecipes.ui.fragments.newRecipe

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.launch
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_recipe.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.AuthUtil
import ua.co.myrecipes.util.RecipeType

@AndroidEntryPoint
class NewRecipeFragment : BaseFragment(R.layout.fragment_new_recipe) {
    private var imgUri: Uri? = null
    private var time = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (AuthUtil.email.isBlank()){
            findNavController().navigate(R.id.action_newRecipeFragment_to_regFragment, bundleOf("redirectToRegister" to true))
        }

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let {
                imgUri = it
                recipe_img.setImageURI(it)
                setImage_tv.hint = ""                           //clear textView
            }
        }

        add_recipe_img.setOnClickListener {
            /*if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                return@setOnClickListener
            }*/

            AlertDialog.Builder(requireContext())
                .setTitle("Choose resource")
                .setMessage("Choose source")
                .setPositiveButton("Camera") { _, _ ->
                    if (isPermissionGranted(CAMERA) && isPermissionGranted(WRITE_EXTERNAL_STORAGE)){
                        takePhoto.launch()
                    } else {
                        requestCameraPermissions.launch(arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE))
                    }
                    takePhoto.launch()
                }
                .setNegativeButton("Gallery") { _, _ ->
                    requestReadExternalPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                .show()
//            cropActivityResultLauncher.launch(null)
        }

        prep_time_btn.setOnClickListener(this::choosingTime)

        type_spinner.adapter =
            ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.recipeTypes))

        to_ingredients_fab.setOnClickListener {
            if (!validateInput(recipe_name_et.text.toString(), recipe_name_til)){
                return@setOnClickListener
            }

            if (new_time_tv.text.isBlank()){
                showSnackBar(R.string.choose_time)
                new_time_tv.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.error))
                return@setOnClickListener
            }

            if (recipe_img.drawable == null){
                showSnackBar(R.string.insert_image)
                setImage_tv.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.error))
                return@setOnClickListener
            }

            val recipe = Recipe().apply {
                name = recipe_name_et.text.toString().trim()
                author = AuthUtil.email.substringBefore("@")
                durationPrepare = time
                type = RecipeType.values()[type_spinner.selectedItemPosition]
                imgUrl = imgUri.toString()
            }
            findNavController().navigate(R.id.action_newRecipeFragment_to_newRecipeIngrFragment, bundleOf("recipe" to recipe))
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.add_new_recipe)
    }

    private fun choosingTime(view: View){
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

    private fun validateInput(string: String, textInputLayout: TextInputLayout) =
        if (string.isEmpty()){
            textInputLayout.error = getString(R.string.the_field_cant_be_empty)
            false
        } else {
            textInputLayout.error = null
            true
        }
}