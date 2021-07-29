package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.co.myrecipes.databinding.ItemDirectionBinding

class DirectionsAdapter(
    var directions: List<String>
): RecyclerView.Adapter<DirectionsAdapter.DirectionsViewHolder>() {

    class DirectionsViewHolder(val binding: ItemDirectionBinding) : RecyclerView.ViewHolder(binding.root){
        val directionNameTv: TextView = binding.directionNameTv
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String) =
            oldItem.hashCode() == newItem.hashCode()
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
            ItemDirectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = items.size
}