package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_direction.view.*
import ua.co.myrecipes.R

class DirectionsAdapter(
    var items: MutableList<String>,
    private val isForNewRecipe: Boolean = true
): RecyclerView.Adapter<DirectionsAdapter.DirectViewHolder>() {

    class DirectViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_direction, parent, false)
        return DirectViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DirectViewHolder, position: Int) {
        val currentDirectItem = items[position]

        holder.itemView.direction_item_tv.text = "${(position + 1)}."
        holder.itemView.direction_name_tv.text = currentDirectItem

        if (!isForNewRecipe){
            holder.itemView.delete_dir_imv.visibility = View.INVISIBLE
        }

        holder.itemView.delete_dir_imv.setOnClickListener {
            items.remove(currentDirectItem)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        }
    }
}