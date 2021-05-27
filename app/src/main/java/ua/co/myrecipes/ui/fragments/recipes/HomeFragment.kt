package ua.co.myrecipes.ui.fragments.recipes

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.RecipeTypeAdapter
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.RecipeType
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    @Inject
    lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var recipeTypeAdapter: RecipeTypeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycleView()
        recipeTypeAdapter.items = RecipeType.values().toMutableList()

        recipeTypeAdapter.setItemClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_recipesFragment, bundleOf("recipeType" to it.name))
        }
    }

    private fun setupRecycleView() = recipeTypes.apply {
        adapter = recipeTypeAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.app_name)
    }
}