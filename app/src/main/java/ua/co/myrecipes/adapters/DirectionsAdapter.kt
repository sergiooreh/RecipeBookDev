package ua.co.myrecipes.adapters

import android.view.View
import androidx.recyclerview.widget.AsyncListDiffer
import kotlinx.android.synthetic.main.item_direction.view.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Ingredient

class DirectionsAdapter(
    private val isForNewRecipe: Boolean = true,
    var directions: MutableList<String>
): BaseAdapter<String>(R.layout.item_direction) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val currentDirectItem = directions[position]

        holder.itemView.direction_name_tv.text = currentDirectItem

        if (!isForNewRecipe){
            holder.itemView.delete_dir_imv.visibility = View.INVISIBLE
        }

        holder.itemView.delete_dir_imv.setOnClickListener {
            directions.remove(currentDirectItem)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        }
    }
}