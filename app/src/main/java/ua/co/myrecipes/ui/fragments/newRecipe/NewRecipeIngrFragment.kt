package ua.co.myrecipes.ui.fragments.newRecipe

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_new_recipe_ingr.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.IngredientsAdapter
import ua.co.myrecipes.model.Ingredient
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.ui.dialogs.AddDialogListenerIngr
import ua.co.myrecipes.ui.dialogs.AddIngrItemDialog
import ua.co.myrecipes.ui.fragments.BaseFragment


class NewRecipeIngrFragment : BaseFragment(R.layout.fragment_new_recipe_ingr) {
    val ingrList = arrayListOf<Ingredient>()
    private lateinit var ingredientsAdapter: IngredientsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()

        add_recipe_ingr_btn.setOnClickListener {
            AddIngrItemDialog(requireContext(),
                object : AddDialogListenerIngr {
                    override fun onAddButtonClick(ingredient: Ingredient) {
                        ingrList.add(ingredient)
                        ingredientsAdapter.notifyDataSetChanged()
                    }
                }).show()
        }

        val recipe = arguments?.getParcelable<Recipe>("recipe")
        recipe?.ingredients = ingrList

        to_directions_fab.setOnClickListener {
            if (ingrList.isEmpty()){
                showSnackBar(R.string.add_ingredients)
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_newRecipeIngrFragment_to_newRecipeDirecFragment, bundleOf("recipe" to recipe))
        }
    }

    private val itemTouchHelperCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {            //direction of swiping

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val item = ingrList[position]
            ingrList.removeAt(position)
            ingredientsAdapter.notifyItemRemoved(position)
            Snackbar.make(requireView(),"Successfully deleted", Snackbar.LENGTH_LONG).apply {
                setAction("Undo"){
                    ingrList.add(position, item)
                    ingredientsAdapter.notifyItemInserted(ingrList.indexOf(item))
                }
                show()
            }
            ingredientsAdapter.notifyDataSetChanged()
        }
    }

    private fun setupRecycleView() = ingredients_rv.apply {
        ingredientsAdapter = IngredientsAdapter(ingrList)
        adapter = ingredientsAdapter
        layoutManager = LinearLayoutManager(requireContext())
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
    }
}