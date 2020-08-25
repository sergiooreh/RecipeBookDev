package ua.co.myrecipes.ui.fragments.newRecipe

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_new_recipe_direc.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.DirectionsAdapter
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.ui.dialogs.AddDialogListenerDir
import ua.co.myrecipes.ui.dialogs.addDirections.AddDirectionsDialog

class NewRecipeDirecFragment : Fragment(R.layout.fragment_new_recipe_direc) {
    val directList = arrayListOf<String>()
    private lateinit var directionsAdapter: DirectionsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycleView()
        directionsAdapter.items = directList

        add_ingr_btn.setOnClickListener {
            AddDirectionsDialog(requireContext(),
            object : AddDialogListenerDir{
                override fun onAddButtonClick(direction: String) {
                    directList.add(direction)
                }
            }).show()
        }

        val recipe =arguments?.getParcelable<Recipe>("recipe")
        recipe?.directions = directList

    }

    private fun setupRecycleView() {
        directionsAdapter = DirectionsAdapter(directList)
        directions_rv.apply {
            adapter = directionsAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }
}