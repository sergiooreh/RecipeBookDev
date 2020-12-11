package ua.co.myrecipes.ui.fragments.recipes

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
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
import ua.co.myrecipes.util.AuthUtil
import ua.co.myrecipes.util.AuthUtil.Companion.uid
import ua.co.myrecipes.util.EventObserver
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
        setHasOptionsMenu(recipe.author == AuthUtil.email.substringBefore("@"))

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeViewModel.loadRecipe(recipe)
        subscribeToObservers()

        recipeAuthor_tv.setOnClickListener {
            findNavController().navigate(R.id.action_recipeFragment_to_profileFragment, bundleOf("userName" to recipeAuthor_tv.text))
        }

        like_btn.isEnabled = AuthUtil.email.isNotBlank()

        like_btn.setOnClickListener {
            if (!recipe.isLiking){
                recipe.isLiked = !recipe.isLiked
                if (recipe.isLiked) recipe.likedBy += uid
                else recipe.likedBy -= uid

                recipeViewModel.toggleLikeForRecipe(recipe)
            }
        }
    }

    private fun subscribeToObservers(){
        recipeViewModel.recipe.observe(viewLifecycleOwner, EventObserver(
            onError = { error ->
                displayProgressBar(progress_bar_recipe, isDisplayed = false)
                showToast(text = error)
            },
            onLoading = { displayProgressBar(progress_bar_recipe) }
        ){
            recipe = it
            it.apply {
                glide.load(imgUrl).into(recipeImg_img)
                recipeName_tv.text = name
                recipeAuthor_tv.text = author
                recipeTime_tv.text = durationPrepare
                recipeLikes_tv.text = likedBy.size.toString()

                if(FirebaseAuth.getInstance().currentUser?.uid in likedBy){
                    recipe.isLiked = true
                    like_btn.setColorFilter((Color.RED))
                } else {
                    recipe.isLiked = false
                    like_btn.setColorFilter(Color.GRAY)
                }
                ingredientsAdapter = IngredientsAdapter(ingredients)
                directionsAdapter = DirectionsAdapter(directions)
                setupRecycleView()
            }
            displayProgressBar(progress_bar_recipe, isDisplayed = false)
        })

        recipeViewModel.likePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                recipe.isLiking = false
            },
            onLoading = {
                recipe.isLiking = true
            }
        ) { isLiked ->
            recipe.isLiking = false
            if(isLiked) {
                like_btn.setColorFilter((Color.RED))

                lifecycleScope.launch {
                    PushNotification(
                        PushNotificationData("RecipeBookApp", "${AuthUtil.email.substringBefore('@')} liked your recipe"),
                        userViewModel.getUserTokenAsync(recipe.author).await()
                    ).also {
                        sendNotification(it)
                    }
                }
            } else {
                like_btn.setColorFilter(Color.GRAY)
            }
            recipeLikes_tv.text = recipe.likedBy.size.toString()
        })
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

    private fun setupRecycleView() {
        ingredients_rv.apply {
            adapter = ingredientsAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = LinearLayoutManager(requireContext())
        }
        directions_rv.apply {
            adapter = directionsAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}