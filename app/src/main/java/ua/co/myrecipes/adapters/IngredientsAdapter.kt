package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_ingredient.view.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Ingredient

class IngredientsAdapter(
    var items: MutableList<Ingredient>,
    private val isForAddRecipe: Boolean = true
): RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
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