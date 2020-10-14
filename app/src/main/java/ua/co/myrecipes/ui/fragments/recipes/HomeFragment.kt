package ua.co.myrecipes.ui.fragments.recipes

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.RecipeTypeAdapter
import ua.co.myrecipes.util.Constants.KEY_FIRST_TIME_ENTER
import ua.co.myrecipes.util.RecipeType
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var recipeTypeAdapter: RecipeTypeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref.edit {
            putBoolean(KEY_FIRST_TIME_ENTER, false)         //new with Android KTX
        }

        setupRecycleView()
        recipeTypeAdapter.differ.submitList(RecipeType.values().toMutableList())

        recipeTypeAdapter.setOnItemClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_recipesFragment, bundleOf("recipeType" to it.name))
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.app_name)
    }

    private fun setupRecycleView() {
        recipeTypeAdapter = RecipeTypeAdapter(requestManager)
        recipeTypes.apply {
            adapter = recipeTypeAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}