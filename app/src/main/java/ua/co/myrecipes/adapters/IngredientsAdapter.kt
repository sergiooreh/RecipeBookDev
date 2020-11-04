package ua.co.myrecipes.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import kotlinx.android.synthetic.main.item_ingredient.view.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Ingredient

class IngredientsAdapter(
    var ingredients: MutableList<Ingredient>
): BaseAdapter<Ingredient>(R.layout.item_ingredient) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val currentIngredientItem = ingredients[position]

        holder.itemView.tvName.text = currentIngredientItem.name
        holder.itemView.tvUnit.text = currentIngredientItem.unit
        holder.itemView.tvAmount.text = currentIngredientItem.amount
    }
}