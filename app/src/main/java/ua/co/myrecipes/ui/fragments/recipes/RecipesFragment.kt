package ua.co.myrecipes.ui.fragments.recipes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipes.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.RecipesAdapter
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.viewmodels.RecipeViewModel
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes){
    private lateinit var recipesAdapter: RecipesAdapter
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycleView()
        val type = RecipeType.valueOf(arguments?.getString("recipeType").toString())
        recipeViewModel.loadRecipes(type)

        getRecipes()

        start_srLayout.apply {
            setOnRefreshListener {
                getRecipes()
                progress_bar.visibility = View.GONE
                start_srLayout.isRefreshing = false
            }
        }
    }

    private fun getRecipes(){
        recipeViewModel.recipes.observe(viewLifecycleOwner, {     //observe - подписываемся (определять состояние Activity/fragment, подписчик, т.е. колбэк, в который LiveData будет отправлять данные)
            // recipesAdapter.differ.submitList(emptyList())
            when(it){
                is DataState.Success<List<Recipe>> ->{
                    displayProgressBar(false)
                    recipesAdapter.differ.submitList(it.data)
                }
                is DataState.Error -> {
                    displayProgressBar(false)
//                    displayError(dataState.exception.message)
                }
                is DataState.Loading -> {
                    displayProgressBar(true)
                }
            }
        })
    }

    /*private fun displayError(message: String?){
       if(message != null) text.text = message else text.text = "Unknown error."
   }*/

    private fun displayProgressBar(isDisplayed: Boolean){
        progress_bar.visibility = if(isDisplayed) View.VISIBLE else View.GONE
    }

    private fun setupRecycleView() {
        recipesAdapter = RecipesAdapter()
        recipes_rv.apply {
            adapter = recipesAdapter
            layoutManager = GridLayoutManager(requireContext(),2)
            setHasFixedSize(true)
        }

    }
}
