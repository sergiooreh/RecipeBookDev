package ua.co.myrecipes.ui.fragments.newRecipe

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_new_recipe.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.RecipeType

class NewRecipeFragment : Fragment(R.layout.fragment_new_recipe) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type_spinner.adapter =
            ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, RecipeType.values())

        to_ingredients_fab.setOnClickListener {
            if (!validateInput(recipe_name_et.text.toString(), recipe_name_til) ||
                (!validateInput(prep_time_et.text.toString(), prep_time_til))){
                return@setOnClickListener
            }

            val recipe = Recipe().apply {
                name = recipe_name_et.text.toString().trim()
                durationPrepare = prep_time_et.text.toString().trim().toInt()
                type = type_spinner.selectedItem as RecipeType
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

}