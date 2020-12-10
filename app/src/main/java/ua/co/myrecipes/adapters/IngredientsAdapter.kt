package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_ingredient.view.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Ingredient

class IngredientsAdapter(
    var ingredients: List<Ingredient>
): RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder>() {

    class IngredientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvName: TextView = itemView.tvName
        val tvUnit: TextView = itemView.tvUnit
        val tvAmount: TextView = itemView.tvAmount
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Ingredient>(){
        override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    private var items: List<Ingredient>
        get() = ingredients
        set(value) = differ.submitList(value)

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        val currentIngredientItem = items[position]
        holder.apply {
            tvName.text = currentIngredientItem.name
            tvUnit.text = currentIngredientItem.unit
            tvAmount.text = currentIngredientItem.amount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        IngredientsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_ingredient,
                parent,
                false
            )
        )

    override fun getItemCount() = items.size
}