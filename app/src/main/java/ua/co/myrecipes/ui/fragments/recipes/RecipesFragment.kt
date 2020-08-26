package ua.co.myrecipes.ui.fragments.recipes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_recipes.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.RecipesAdapter
import ua.co.myrecipes.ui.RecipeViewModel
import ua.co.myrecipes.util.RecipeType

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes){
    private lateinit var recipesAdapter: RecipesAdapter
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycleView()
        val type = RecipeType.valueOf(arguments?.getString("recipeType").toString())
        recipeViewModel.loadRecipes(type).observe(viewLifecycleOwner, Observer {
            recipesAdapter.differ.submitList(it)
        })
    }

    private fun setupRecycleView() {
        recipesAdapter = RecipesAdapter()
        recipes_rv.apply {
            adapter = recipesAdapter
            layoutManager = GridLayoutManager(requireContext(),2)
        }

    }
}
