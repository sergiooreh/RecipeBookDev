package ua.co.myrecipes.ui.fragments.recipes

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipe.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.DirectionsAdapter
import ua.co.myrecipes.adapters.IngredientsAdapter
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.notification.PushNotification
import ua.co.myrecipes.notification.PushNotificationData
import ua.co.myrecipes.notification.api.RetrofitInstance
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.DataState
import ua.co.myrecipes.viewmodels.RecipeViewModel
import ua.co.myrecipes.viewmodels.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class RecipeFragment : BaseFragment(R.layout.fragment_recipe) {
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var directionsAdapter: DirectionsAdapter
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var recipe: Recipe

    @Inject
    lateinit var glide: RequestManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recipe = arguments?.getParcelable("recipe")!!
        if (recipe.author == userViewModel.getUserEmail().substringBefore("@")){
            setHasOptionsMenu(true)
        } else {
            setHasOptionsMenu(false)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeViewModel.loadRecipe(recipe)
        subscribeToObservers()

        recipeAuthor_tv.setOnClickListener {
            findNavController().navigate(R.id.action_recipeFragment_to_profileFragment, bundleOf("userName" to recipeAuthor_tv.text))
        }
    }

    private fun subscribeToObservers(){
        recipeViewModel.recipe.observe(viewLifecycleOwner, {
            when(it){
                is DataState.Success<Recipe> ->{
                    it.data.apply {
                        glide.load(imgUrl).into(recipeImg_img)
                        recipeName_tv.text = name
                        recipeAuthor_tv.text = author
                        recipeTime_tv.text = durationPrepare
                        recipeLikes_tv.text = userLiked.size.toString()

                        handleLikeBtn(this)

                        ingredientsAdapter = IngredientsAdapter(ingredients)
                        directionsAdapter = DirectionsAdapter(directions)
                        ingredientsAdapter.items = ingredients
                        directionsAdapter.items = directions
                        setupRecycleView(directions_rv,directionsAdapter,1)
                        setupRecycleView(ingredients_rv,ingredientsAdapter,1)
                    }
                    displayProgressBar(progress_bar_recipe)
                }
                is DataState.Error -> {
                    displayProgressBar(progress_bar_recipe)
                    showToast(text = it.exception.message ?: getString(R.string.an_unknown_error_occurred))
                }
                is DataState.Loading -> {
                    displayProgressBar(progress_bar_recipe, isDisplayed = true)
                }
            }
        })
    }

    private fun handleLikeBtn(recipe: Recipe){
        if (userViewModel.getUserEmail().isBlank()){
            like_btn.isEnabled = false
        } else{
            var isLiked = false
            lifecycleScope.launch {
                isLiked = recipeViewModel.isLikedRecipeAsync(recipe).await()
                if (isLiked) like_btn.setColorFilter((Color.RED))
            }
            like_btn.setOnClickListener {
                if (isLiked){
                    recipeViewModel.removeLikedRecipe(recipe).invokeOnCompletion {
                        like_btn.setColorFilter(Color.GRAY)
                        isLiked = false
                        recipeLikes_tv.text = recipeLikes_tv.text.toString().toInt().minus(1).toString()
                    }
                } else{
                    recipeViewModel.addLikedRecipe(recipe).invokeOnCompletion {
                        recipeLikes_tv.text = recipeLikes_tv.text.toString().toInt().plus(1).toString()
                        like_btn.setColorFilter(Color.RED)
                        isLiked = true
                        lifecycleScope.launch {
                            PushNotification(
                                PushNotificationData("RecipeBookApp", "${userViewModel.getUserEmail().substringBefore('@')} liked your recipe"),
                                userViewModel.getUserTokenAsync(recipe.author).await()
                            ).also {
                                sendNotification(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try { RetrofitInstance.api.postNotification(notification) }
        catch (e: Exception){ showToast(text = e.message ?: getString(R.string.an_unknown_error_occurred))}
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.deleteRecipe -> {
                recipeViewModel.deleteRecipe(recipe).invokeOnCompletion {
                    showSnackBar(R.string.recipe_successfully_deleted)
                    findNavController().popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.recipe_menu, menu)
    }
}