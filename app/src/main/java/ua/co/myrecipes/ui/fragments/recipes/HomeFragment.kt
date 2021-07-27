package ua.co.myrecipes.ui.fragments.recipes

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.RecipeTypeAdapter
import ua.co.myrecipes.databinding.FragmentHomeBinding
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.util.RecipeType
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentHomeBinding::inflate

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

    private fun setupRecycleView() = binding.recipeTypes.apply {
        adapter = recipeTypeAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.app_name)
    }
}