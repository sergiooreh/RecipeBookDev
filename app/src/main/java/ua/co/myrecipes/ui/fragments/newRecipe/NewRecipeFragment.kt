package ua.co.myrecipes.ui.fragments.newRecipe

import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import ua.co.myrecipes.R
import ua.co.myrecipes.databinding.FragmentNewRecipeBinding
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.AuthUtil
import ua.co.myrecipes.util.RecipeType

@AndroidEntryPoint
class NewRecipeFragment : BaseFragment<FragmentNewRecipeBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentNewRecipeBinding::inflate

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
                binding.recipeImg.setImageURI(it)
                binding.setImageTv.hint = ""                           //clear textView
            }
        }

        binding.addRecipeImg.setOnClickListener {
            openImageSource()
        }

        binding.prepTimeBtn.setOnClickListener(this::choosingTime)

        binding.typeSpinner.adapter =
            ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.recipeTypes))

        binding.toIngredientsFab.setOnClickListener {
            if (!validateInput(binding.recipeNameEt.text.toString(), binding.recipeNameTil)){
                return@setOnClickListener
            }

            if (binding.newTimeTv.text.isBlank()){
                showSnackBar(R.string.choose_time)
                binding.newTimeTv.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.error))
                return@setOnClickListener
            }

            if (binding.recipeImg.drawable == null){
                showSnackBar(R.string.insert_image)
                binding.setImageTv.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.error))
                return@setOnClickListener
            }

            val recipe = Recipe().apply {
                name = binding.recipeNameEt.text.toString().trim()
                author = AuthUtil.email.substringBefore("@")
                durationPrepare = time
                type = RecipeType.values()[binding.typeSpinner.selectedItemPosition]
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
        val timePickerDialog = TimePickerDialog(requireContext(),
            { _, h, m ->
                time = when {
                    h==0 -> "$m ${getString(R.string.min)}"
                    m==0 -> "$h ${getString(R.string.hour)}"
                    else -> "$h ${getString(R.string.hour)}  $m ${getString(R.string.min)}"
                }
                binding.newTimeTv.text = time
            }, 0, 0, true
        )
//        timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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