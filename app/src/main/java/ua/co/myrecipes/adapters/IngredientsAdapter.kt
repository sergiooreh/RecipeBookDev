package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_ingredient.view.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Ingredient

class IngredientsAdapter(
    var items: ArrayList<Ingredient>,
): RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val currentIngredientItem = items[position]

        holder.itemView.tvName.text = currentIngredientItem.name
        holder.itemView.tvAmount.text = "${currentIngredientItem.amount}"
        holder.itemView.tvUnit.text = currentIngredientItem.unit

        holder.itemView.ivDelete.setOnClickListener {
            items.remove(currentIngredientItem)
            notifyItemRemoved(position)
        }

        holder.itemView.ivPlus.setOnClickListener {
            currentIngredientItem.amount += 1
            notifyDataSetChanged()
        }

        holder.itemView.ivMinus.setOnClickListener {
            if (currentIngredientItem.amount > 0){
                currentIngredientItem.amount -= 1
                notifyDataSetChanged()
            }
        }
    }
}