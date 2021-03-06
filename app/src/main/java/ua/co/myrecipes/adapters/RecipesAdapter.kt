package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import ua.co.myrecipes.databinding.ItemRecipesBinding
import ua.co.myrecipes.model.Recipe
import javax.inject.Inject

class RecipesAdapter @Inject constructor(
    private val glide: RequestManager
): RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>()  {

    class RecipesViewHolder(val binding: ItemRecipesBinding) : RecyclerView.ViewHolder(binding.root){
        val recipeNameTv: TextView = binding.recipeNameTv
        val recipeAuthorTv: TextView = binding.recipeAuthorTv
        val imageView: ImageView = binding.imageView
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Recipe>(){
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) =
           oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var items: List<Recipe>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val recipe = differ.currentList[position]
        holder.apply {
            recipeNameTv.text = recipe.name
            recipeAuthorTv.text = recipe.author
            glide.load(recipe.imgUrl).into(imageView)

            itemView.setOnClickListener {
                onItemClickListener?.let { it(recipe) }
            }
        }
    }

    private var onItemClickListener: ((Recipe) -> Unit)? = null

    fun setItemClickListener(listener: (Recipe) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecipesViewHolder(
            ItemRecipesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = items.size
}