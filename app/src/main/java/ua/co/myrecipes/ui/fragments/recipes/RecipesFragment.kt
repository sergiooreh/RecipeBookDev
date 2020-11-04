package ua.co.myrecipes.ui.fragments.recipes

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipes.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.RecipesAdapter
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.util.RecipeType
import ua.co.myrecipes.viewmodels.RecipeViewModel
import ua.co.myrecipes.viewmodels.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class RecipesFragment : BaseFragment(R.layout.fragment_recipes){
    @Inject
    lateinit var requestManager: RequestManager
    private lateinit var recipesAdapter: RecipesAdapter
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipesAdapter = RecipesAdapter(requestManager)
        setupRecycleView(recipes_rv,recipesAdapter,2)

        when(val recipesAuthor = arguments?.getString("recipeAuthor") ?: ""){
            "" -> {
                val type = RecipeType.valueOf(arguments?.getString("recipeType").toString())
                recipeViewModel.loadRecipesByType(type)
            }
            userViewModel.getUserEmail() -> {
                recipeViewModel.loadCurrentUserRecipes()
            }
            else -> if (recipesAuthor.startsWith("@")) {
                recipeViewModel.loadMyLikedRecipes()
                } else{
                    recipeViewModel.loadRecipesByUserName(recipesAuthor)
                }
        }

        subscribeToObservers()

        start_srLayout.apply {
            setOnRefreshListener {
                subscribeToObservers()
                progress_bar.visibility = View.GONE
                start_srLayout.isRefreshing = false
            }
        }

        recipesAdapter.setItemClickListener {
            findNavController().navigate(R.id.action_recipesFragment_to_recipeFragment, bundleOf("recipe" to it))
        }
    }

    private fun subscribeToObservers(){
        recipeViewModel.recipes.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success<List<Recipe>> ->{
                    displayProgressBar(progress_bar)
                    recipesAdapter.items = it.data.toMutableList()
                }
                is DataState.Error -> {
                    displayProgressBar(progress_bar)
                    showToast(text = it.exception.message ?: getString(R.string.an_unknown_error_occurred))
                }
                is DataState.Loading -> {
                    displayProgressBar(progress_bar, isDisplayed = true)
                }
            }
        })
    }
}
