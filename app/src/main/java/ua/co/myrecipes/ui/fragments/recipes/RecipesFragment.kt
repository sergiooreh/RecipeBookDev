package ua.co.myrecipes.ui.fragments.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.RecipesAdapter
import ua.co.myrecipes.databinding.FragmentRecipesBinding
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.AuthUtil
import ua.co.myrecipes.util.EventObserver
import ua.co.myrecipes.util.RecipeType
import ua.co.myrecipes.viewmodels.RecipeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class RecipesFragment : BaseFragment<FragmentRecipesBinding>(){
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentRecipesBinding::inflate

    @Inject
    lateinit var recipesAdapter: RecipesAdapter
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()

        loadSpecificRecipes()

        displayProgressBar(binding.progressBar)
        subscribeToObservers()

        binding.startSrLayout.apply {
            setOnRefreshListener {
                loadSpecificRecipes()
                binding.progressBar.visibility = View.GONE
                binding.startSrLayout.isRefreshing = false
            }
        }

        recipesAdapter.setItemClickListener {
            findNavController().navigate(R.id.action_recipesFragment_to_recipeFragment, bundleOf("recipe" to it))
        }
    }

    private fun subscribeToObservers(){
        recipeViewModel.recipes.observe(viewLifecycleOwner, EventObserver(
            onError = {
                displayProgressBar(binding.progressBar, isDisplayed = false)
                showToast(text = it)
            }
        ){ list ->
            displayProgressBar(binding.progressBar, isDisplayed = false)
            recipesAdapter.items = list.toMutableList()
        })
    }

    private fun setupRecycleView() = binding.recipesRv.apply {
        adapter = recipesAdapter
        layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
    }

    private fun loadSpecificRecipes(){
        when(val recipesAuthor = arguments?.getString("recipeAuthor") ?: ""){
            "" -> {                                                                                             //type
                val type = RecipeType.valueOf(arguments?.getString("recipeType").toString())
                recipeViewModel.loadRecipesByType(type)
            }
            AuthUtil.email -> { recipeViewModel.loadCurrentUserRecipes() }                                       //my recipes
            else -> if (recipesAuthor.startsWith("@")) { recipeViewModel.loadMyLikedRecipes() }            //liked recipes
                    else{ recipeViewModel.loadRecipesByUserName(recipesAuthor) }                                 //user's recipes
        }
    }
}
