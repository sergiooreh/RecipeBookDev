package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.co.myrecipes.databinding.ItemIngredientBinding
import ua.co.myrecipes.model.Ingredient

class IngredientsAdapter(
    var ingredients: List<Ingredient>
): RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder>() {

    class IngredientsViewHolder(val binding: ItemIngredientBinding) : RecyclerView.ViewHolder(binding.root){
        val tvName: TextView = binding.tvName
        val tvUnit: TextView = binding.tvUnit
        val tvAmount: TextView = binding.tvAmount
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Ingredient>(){
        override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient) =
            oldItem.hashCode() == newItem.hashCode()
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
            ItemIngredientBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = items.size
}