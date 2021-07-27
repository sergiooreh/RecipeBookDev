package ua.co.myrecipes.ui.fragments.recipes

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.DirectionsAdapter
import ua.co.myrecipes.adapters.IngredientsAdapter
import ua.co.myrecipes.databinding.FragmentRecipeBinding
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
class RecipeFragment : BaseFragment<FragmentRecipeBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentRecipeBinding::inflate

    private val recipeViewModel: RecipeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var directionsAdapter: DirectionsAdapter
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var recipe: Recipe

    @Inject
    lateinit var glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipe = arguments?.getParcelable("recipe")!!
        setHasOptionsMenu(recipe.author == AuthUtil.email.substringBefore("@"))

        recipeViewModel.loadRecipe(recipe)
        subscribeToObservers()

        binding.recipeAuthorTv.setOnClickListener {
            findNavController().navigate(R.id.action_recipeFragment_to_profileFragment, bundleOf("userName" to binding.recipeAuthorTv.text))
        }

        binding.likeBtn.isEnabled = AuthUtil.email.isNotBlank()

        binding.likeBtn.setOnClickListener {
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
                displayProgressBar(binding.progressBarRecipe, isDisplayed = false)
                showToast(text = error)
            },
            onLoading = { displayProgressBar(binding.progressBarRecipe) }
        ){
            recipe = it
            it.apply {
                glide.load(imgUrl).into(binding.recipeImgImg)
                binding.recipeNameTv.text = name
                binding.recipeAuthorTv.text = author
                binding.recipeTimeTv.text = durationPrepare
                binding.recipeLikesTv.text = likedBy.size.toString()

                if(FirebaseAuth.getInstance().currentUser?.uid in likedBy){
                    recipe.isLiked = true
                    binding.likeBtn.setColorFilter((Color.RED))
                } else {
                    recipe.isLiked = false
                    binding.likeBtn.setColorFilter(Color.GRAY)
                }
                ingredientsAdapter = IngredientsAdapter(ingredients)
                directionsAdapter = DirectionsAdapter(directions)
                setupRecycleView()
            }
            displayProgressBar(binding.progressBarRecipe, isDisplayed = false)
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
                binding.likeBtn.setColorFilter((Color.RED))

                sendNotification()
            } else {
                binding.likeBtn.setColorFilter(Color.GRAY)
            }
            binding.recipeLikesTv.text = recipe.likedBy.size.toString()
        })
    }

    private fun sendNotification() {
        lifecycleScope.launch {
            PushNotification(
                PushNotificationData(
                    "RecipeBookApp",
                    "${AuthUtil.email.substringBefore('@')} liked your recipe"
                ),
                userViewModel.getUserTokenAsync(recipe.author).await()
            ).also {
                CoroutineScope(Dispatchers.IO).launch {
                    try { RetrofitInstance.api.postNotification(it) }
                    catch (e: Exception) {
                        showToast(text = e.message ?: getString(R.string.an_unknown_error_occurred))
                    }
                }
            }
        }
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
        binding.ingredientsRv.apply {
            adapter = ingredientsAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.directionsRv.apply {
            adapter = directionsAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}