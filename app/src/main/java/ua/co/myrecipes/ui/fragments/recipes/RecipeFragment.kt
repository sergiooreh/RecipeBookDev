package ua.co.myrecipes.ui.fragments.recipes

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipe.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.DirectionsAdapter
import ua.co.myrecipes.adapters.IngredientsAdapter
import ua.co.myrecipes.model.Ingredient
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.viewmodels.RecipeViewModel

@AndroidEntryPoint
class RecipeFragment : Fragment(R.layout.fragment_recipe) {
    private val recipeViewModel: RecipeViewModel by viewModels()

    private lateinit var directionsAdapter: DirectionsAdapter
    private lateinit var ingredientsAdapter: IngredientsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe = arguments?.getParcelable<Recipe>("recipe")!!
        recipeViewModel.loadRecipe(recipe)

        getRecipe()

        recipeAuthor_tv.setOnClickListener {
            findNavController().navigate(R.id.action_recipeFragment_to_profileFragment, bundleOf("userName" to recipeAuthor_tv.text))
        }

        follow_btn.setOnClickListener {

        }
    }

    private fun getRecipe(){
        recipeViewModel.recipe.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success<Recipe> ->{
                    it.data.apply {
                        recipeName_tv.text = name
                        recipeAuthor_tv.text = author
                        recipeTime_tv.text = durationPrepare
                        Picasso.get()
                            .load(img)
                            .fit()
                            .placeholder(R.drawable.ic_broken)
                            .into(recipeImg_img)
                        setupRecycleView(ingredients,directions)
                    }
                    displayProgressBar(false)
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

    private fun displayProgressBar(isDisplayed: Boolean){
        progress_bar_recipe.visibility = if(isDisplayed) View.VISIBLE else View.GONE
    }

    private fun setupRecycleView(ingr: List<Ingredient>, direct: List<String>) {
        ingredientsAdapter = IngredientsAdapter(ingr,false)
        directionsAdapter = DirectionsAdapter(direct, false)
        directions_rv.apply {
            adapter = directionsAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        ingredients_rv.apply {
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }
}