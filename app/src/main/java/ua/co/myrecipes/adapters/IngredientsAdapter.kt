package ua.co.myrecipes.adapters

import android.view.View
import androidx.recyclerview.widget.AsyncListDiffer
import kotlinx.android.synthetic.main.item_ingredient.view.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Ingredient

class IngredientsAdapter(
    private val isForAddRecipe: Boolean = true
): BaseAdapter<Ingredient>(R.layout.item_ingredient) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val currentIngredientItem = items[position]

        if (currentIngredientItem.amount.isNotBlank()){
            holder.itemView.tvAmount.text = currentIngredientItem.amount
        } else {
            holder.itemView.tvAmount.text = ""

            holder.itemView.ivPlus.visibility = View.GONE
            holder.itemView.ivMinus.visibility = View.GONE
        }

        holder.itemView.tvName.text = currentIngredientItem.name
        holder.itemView.tvUnit.text = currentIngredientItem.unit

        if (!isForAddRecipe){
            holder.itemView.ivDelete.visibility = View.GONE
            holder.itemView.ivPlus.visibility = View.GONE
            holder.itemView.ivMinus.visibility = View.GONE
        }

        holder.itemView.ivDelete.setOnClickListener {
            items.remove(currentIngredientItem)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        }

        holder.itemView.ivPlus.setOnClickListener {
            currentIngredientItem.amount = currentIngredientItem.amount.toInt().plus(1).toString()
            notifyDataSetChanged()
        }

        holder.itemView.ivMinus.setOnClickListener {
            if (currentIngredientItem.amount.toInt() > 1){
                currentIngredientItem.amount = currentIngredientItem.amount.toInt().minus(1).toString()
                notifyDataSetChanged()
            }
        }
    }
}