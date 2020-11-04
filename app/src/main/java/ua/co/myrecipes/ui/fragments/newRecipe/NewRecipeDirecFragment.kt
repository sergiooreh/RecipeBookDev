package ua.co.myrecipes.ui.fragments.newRecipe

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_recipe_direc.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.DirectionsAdapter
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.viewmodels.RecipeViewModel

@AndroidEntryPoint
class NewRecipeDirecFragment : BaseFragment(R.layout.fragment_new_recipe_direc) {
    private val directList = arrayListOf<String>()
    private lateinit var directionsAdapter: DirectionsAdapter
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        directionsAdapter = DirectionsAdapter(directList)
        directionsAdapter.items = directList
        setupRecycleView(directions_rv, directionsAdapter, 0, directList)

        add_ingr_btn.setOnClickListener {
            addDirectionDialog()
        }

        val recipe = arguments?.getParcelable<Recipe>("recipe")
        recipe?.directions = directList

        finish_add_recipe_btn.setOnClickListener {
            if (directList.isEmpty()){
                showSnackBar(R.string.add_directions)
                return@setOnClickListener
            }
            recipe?.let { recipe ->
                finish_add_recipe_btn.isClickable = false
                recipeViewModel.insertRecipe(recipe).invokeOnCompletion {
                    showSnackBar(R.string.recipe_added)
                    findNavController().navigate(R.id.action_newRecipeDirecFragment_to_homeFragment)
                }
            }
        }
    }

    private fun addDirectionDialog() {
        val editText = EditText(requireContext()).apply {
            isSingleLine = false
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.START or Gravity.TOP
            isHorizontalScrollBarEnabled = true
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(300))                //max length
        }

        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.new_direction)
            setView(editText)
            setPositiveButton(R.string.ADD) { _, _ ->
                if (editText.text.toString().isNotEmpty()){
                    directList.add(editText.text.toString())
                } else{
                    showToast(R.string.please_enter_the_direction)
                }
            }
            setNegativeButton(R.string.CANCEL) { dialogInterface, _ -> dialogInterface.cancel() }
            show()
        }
    }
}