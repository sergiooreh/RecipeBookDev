package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_direction.view.*
import ua.co.myrecipes.R

class DirectionsAdapter(
    var directions: List<String>
): RecyclerView.Adapter<DirectionsAdapter.DirectionsViewHolder>() {

    class DirectionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val directionNameTv: TextView = itemView.direction_name_tv
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    private var items: List<String>
        get() = directions
        set(value) = differ.submitList(value)

    override fun onBindViewHolder(holder: DirectionsViewHolder, position: Int) {
        holder.apply {
            directionNameTv.text = items[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DirectionsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_direction,
                parent,
                false
            )
        )

    override fun getItemCount() = items.size
}