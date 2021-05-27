package ua.co.myrecipes.ui.fragments.newRecipe

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_recipe_direc.*
import ua.co.myrecipes.R
import ua.co.myrecipes.adapters.DirectionsAdapter
import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.ui.fragments.BaseFragment
import ua.co.myrecipes.viewmodels.RecipeViewModel

@AndroidEntryPoint
class NewRecipeDirecFragment : BaseFragment(R.layout.fragment_new_recipe_direc) {
    private val directList = arrayListOf<String>()
    private lateinit var directionsAdapter: DirectionsAdapter
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()

        add_ingr_btn.setOnClickListener {
            addDirectionDialog()
        }

        val recipe = arguments?.getParcelable<Recipe>("recipe")
        recipe?.directions = directList

        finish_add_recipe_btn.setOnClickListener {
            if (directList.isEmpty()){
                showSnackBar(R.string.add_directions)
                return@setOnClickListener
            }
            recipe?.let { recipe ->
                finish_add_recipe_btn.isClickable = false
                recipeViewModel.insertRecipe(recipe).invokeOnCompletion {
                    showSnackBar(R.string.recipe_added)
                    findNavController().navigate(R.id.action_newRecipeDirecFragment_to_homeFragment)
                }
            }
        }
    }

    private val itemTouchHelperCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val item = directList[position]
            directList.removeAt(position)
            directionsAdapter.notifyItemRemoved(position)
            Snackbar.make(requireView(),"Successfully deleted", Snackbar.LENGTH_LONG).apply {
                setAction("Undo"){
                    directList.add(position, item)
                    directionsAdapter.notifyItemInserted(directList.indexOf(item))
                }
                show()
            }
            directionsAdapter.notifyDataSetChanged()
        }
    }

    private fun setupRecycleView() = directions_rv.apply {
        directionsAdapter = DirectionsAdapter(directList)
        adapter = directionsAdapter
        layoutManager = LinearLayoutManager(requireContext())
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
    }


    private fun addDirectionDialog() {
        val editText = EditText(requireContext()).apply {
            isSingleLine = false
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.START or Gravity.TOP
            isHorizontalScrollBarEnabled = true
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(300))                //max length
        }

        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.new_direction)
            setView(editText)
            setPositiveButton(R.string.ADD) { _, _ ->
                if (editText.text.toString().isNotEmpty()){
                    directList.add(editText.text.toString())
                    directionsAdapter.notifyDataSetChanged()
                } else{
                    showToast(R.string.please_enter_the_direction)
                }
            }
            setNegativeButton(R.string.CANCEL) { dialogInterface, _ -> dialogInterface.cancel() }
            show()
        }
    }
}