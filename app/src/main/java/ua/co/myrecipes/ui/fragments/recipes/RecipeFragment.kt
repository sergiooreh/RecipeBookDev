package ua.co.myrecipes.ui.fragments.recipes

import android.graphics.Color
import android.os.Bundle
import android.view.View
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

const val TOPIC = "/topics/myTopic"

@AndroidEntryPoint
class RecipeFragment : BaseFragment(R.layout.fragment_recipe) {
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var directionsAdapter: DirectionsAdapter
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private var isLiked = false

    @Inject
    lateinit var glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe = arguments?.getParcelable<Recipe>("recipe")!!
        recipeViewModel.loadRecipe(recipe)
        getRecipe()

        recipeAuthor_tv.setOnClickListener {
            findNavController().navigate(R.id.action_recipeFragment_to_profileFragment, bundleOf("userName" to recipeAuthor_tv.text))
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
                        glide.load(imgUrl).into(recipeImg_img)

                        handleLikeBtn(this)
                        ingredientsAdapter = IngredientsAdapter(false)
                        directionsAdapter = DirectionsAdapter(false)
                        ingredientsAdapter.items = ingredients.toMutableList()
                        directionsAdapter.items = directions.toMutableList()
                        setupRecycleView(directions_rv,directionsAdapter,1)
                        setupRecycleView(ingredients_rv,ingredientsAdapter,1)
                    }
                    displayProgressBar(progress_bar_recipe)
                }
                is DataState.Error -> {
                    displayProgressBar(progress_bar_recipe)
                    showToast(text = it.exception.message ?: "An unknown error")
                }
                is DataState.Loading -> {
                    displayProgressBar(progress_bar_recipe, isDisplayed = true)
                }
            }
        })
    }

    private fun handleLikeBtn(recipe: Recipe){
        if (userViewModel.getUserEmail().isBlank()){
            like_btn.visibility = View.GONE
        } else{
            lifecycleScope.launch {
                isLiked = recipeViewModel.isLikedRecipeAsync(recipe).await()
                if (isLiked) like_btn.setColorFilter((Color.RED))
            }
            like_btn.setOnClickListener {
                if (isLiked){
                    recipeViewModel.removeLikedRecipe(recipe)
                    like_btn.setColorFilter(Color.GRAY)
                    isLiked = false
                } else{
                    recipeViewModel.addLikedRecipe(recipe)
                    like_btn.setColorFilter(Color.RED)
                    isLiked = true

                    lifecycleScope.launch {
                        //FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)         //subscribe
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

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try { RetrofitInstance.api.postNotification(notification) }
        catch (e: Exception){ showToast(text = e.message ?: "An unknown error")} //TODO
    }
}