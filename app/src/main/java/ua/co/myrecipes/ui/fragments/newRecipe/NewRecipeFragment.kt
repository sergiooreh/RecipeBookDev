package ua.co.myrecipes.ui.fragments.newRecipe

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_new_recipe.*
import ua.co.myrecipes.R

class NewRecipeFragment : Fragment(R.layout.fragment_new_recipe) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        add_recipe_next_fab.setOnClickListener {
            findNavController().navigate(R.id.action_newRecipeFragment_to_newRecipeIngrFragment)
        }

    }

    override fun onResume() {
        super.onResume()
        activity?.title = "Add new recipe"
    }
}