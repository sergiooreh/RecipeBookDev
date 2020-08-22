package ua.co.myrecipes.ui.fragments.newRecipe

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_new_recipe_ingr.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.IngredientsAdapter
import ua.co.myrecipes.model.Ingredient
import ua.co.myrecipes.ui.dialogs.AddDialogListener
import ua.co.myrecipes.ui.dialogs.AddIngrItemDialog


class NewRecipeIngrFragment : Fragment(R.layout.fragment_new_recipe_ingr) {
    val ingredients = arrayListOf<Ingredient>()
    private lateinit var ingredientsAdapter: IngredientsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycleView()

        ingredientsAdapter.items = ingredients

        add_recipe_ingr_fab.setOnClickListener {
            AddIngrItemDialog(requireContext(),
                object : AddDialogListener {
                    override fun onAddButtonClick(ingredient: Ingredient) {
                        ingredients.add(ingredient)
                    }
                }).show()
        }
    }

    private fun setupRecycleView() {
        ingredientsAdapter = IngredientsAdapter(ingredients)
        ingredients_rv.apply {
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }
}