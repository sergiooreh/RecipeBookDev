package ua.co.myrecipes.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.RecipeTypeAdapter
import ua.co.myrecipes.util.RecipeType


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var recipeTypeAdapter: RecipeTypeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycleView()

        recipeTypeAdapter.differ.submitList(RecipeType.values().toMutableList())
    }

    private fun setupRecycleView() {
        recipeTypeAdapter = RecipeTypeAdapter()
        recipeTypes.apply {
            adapter = recipeTypeAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

    }

}