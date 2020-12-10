package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_recipes.view.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Recipe
import javax.inject.Inject

class RecipesAdapter @Inject constructor(
    private val glide: RequestManager
): RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>()  {

    class RecipesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val recipeNameTv: TextView = itemView.recipeName_tv
        val recipeAuthorTv: TextView = itemView.recipeAuthor_tv
        val imageView: ImageView = itemView.imageView
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Recipe>(){
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
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
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_recipes,
                parent,
                false
            )
        )

    override fun getItemCount() = items.size
}