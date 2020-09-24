package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
): RecyclerView.Adapter<RecipesAdapter.MyViewHolder>()  {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_recipes,
                parent,
                false
            )
        )

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val recipe = differ.currentList[position]
        holder.itemView.apply {
            recipeName_tv.text = recipe.name
            recipeAuthor_tv.text = recipe.author
            duration_tv.text = recipe.durationPrepare
            glide.load(recipe.img).into(imageView)

            setOnClickListener {
                onItemClickListener?.let { it(recipe) }
            }
        }
    }

    private var onItemClickListener: ((Recipe) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Recipe) -> Unit)) { onItemClickListener = listener }

    private val differCallback = object : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.name == newItem.name     //we could check by id, but we use api articles, there can be different id
        }
        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)       //takes two our list and compares them(it's ASYNC)
}