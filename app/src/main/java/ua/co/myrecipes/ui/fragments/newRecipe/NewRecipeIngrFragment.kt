package ua.co.myrecipes.ui.fragments.newRecipe

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_new_recipe_ingr.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.IngredientsAdapter
import ua.co.myrecipes.model.Ingredient
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.ui.dialogs.AddDialogListenerIngr
import ua.co.myrecipes.ui.dialogs.AddIngrItemDialog
import ua.co.myrecipes.ui.fragments.BaseFragment


class NewRecipeIngrFragment : BaseFragment(R.layout.fragment_new_recipe_ingr) {
    val ingrList = arrayListOf<Ingredient>()
    private lateinit var ingredientsAdapter: IngredientsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ingredientsAdapter = IngredientsAdapter(ingredients = ingrList)
        ingredientsAdapter.items = ingrList
        setupRecycleView(ingredients_rv,ingredientsAdapter, 0)

        add_recipe_ingr_btn.setOnClickListener {
            AddIngrItemDialog(requireContext(),
                object : AddDialogListenerIngr {
                    override fun onAddButtonClick(ingredient: Ingredient) {
                        ingrList.add(ingredient)
                    }
                }).show()
        }

        val recipe = arguments?.getParcelable<Recipe>("recipe")
        recipe?.ingredients = ingrList

        to_directions_fab.setOnClickListener {
            if (ingrList.isEmpty()){
                showSnackBar(R.string.add_ingredients)
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_newRecipeIngrFragment_to_newRecipeDirecFragment, bundleOf("recipe" to recipe))
        }
    }
}