package ua.co.myrecipes.ui.fragments.newRecipe

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_recipe_direc.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.DirectionsAdapter
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.viewmodels.RecipeViewModel
import ua.co.myrecipes.ui.dialogs.AddDialogListenerDir
import ua.co.myrecipes.ui.dialogs.AddDirectionsDialog
import ua.co.myrecipes.ui.fragments.BaseFragment

@AndroidEntryPoint
class NewRecipeDirecFragment : BaseFragment(R.layout.fragment_new_recipe_direc) {
    val directList = arrayListOf<String>()
    private lateinit var directionsAdapter: DirectionsAdapter
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        directionsAdapter = DirectionsAdapter(directions = directList)
        directionsAdapter.items = directList
        setupRecycleView(directions_rv, directionsAdapter, 0)

        add_ingr_btn.setOnClickListener {
            AddDirectionsDialog(requireContext(),
            object : AddDialogListenerDir{
                override fun onAddButtonClick(direction: String) {
                    directList.add(direction)
                }
            }).show()
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
                recipeViewModel.insertRecipe(recipe)
                showSnackBar(R.string.recipe_added)
                findNavController().navigate(R.id.action_newRecipeDirecFragment_to_homeFragment)
            }
        }
    }
}