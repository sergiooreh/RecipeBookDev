package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import ua.co.myrecipes.R
import ua.co.myrecipes.databinding.ItemRecipetypeBinding
import ua.co.myrecipes.util.RecipeType
import javax.inject.Inject

class RecipeTypeAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<RecipeTypeAdapter.RecipeTypeViewHolder>() {

    class RecipeTypeViewHolder(val binding: ItemRecipetypeBinding) : RecyclerView.ViewHolder(binding.root){
        val recipeTypeTv: TextView = binding.recipeTypeTv
        val recipeTypeImv: ImageView = binding.recipeTypeImv
    }

    private val diffCallback = object : DiffUtil.ItemCallback<RecipeType>(){
        override fun areItemsTheSame(oldItem: RecipeType, newItem: RecipeType) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: RecipeType, newItem: RecipeType) =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var items: List<RecipeType>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    override fun onBindViewHolder(holder: RecipeTypeViewHolder, position: Int) {
        val recipeType = differ.currentList[position]
        holder.apply {
            recipeTypeTv.text = this.itemView.resources.getStringArray(R.array.recipeTypes)[position]
            glide.load(recipeType.img).into(recipeTypeImv)
            itemView.setOnClickListener {
                onItemClickListener?.let { it(recipeType) }
            }
        }
    }

    private var onItemClickListener: ((RecipeType) -> Unit)? = null

    fun setItemClickListener(listener: (RecipeType) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecipeTypeViewHolder(
            ItemRecipetypeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = items.size
}