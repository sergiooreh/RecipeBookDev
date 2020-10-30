package ua.co.myrecipes.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_recipes.view.*
import ua.co.myrecipes.R
import ua.co.myrecipes.model.Recipe
import javax.inject.Inject

class RecipesAdapter @Inject constructor(
    private val glide: RequestManager
): BaseAdapter<Recipe>(R.layout.item_recipes)  {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val recipe = differ.currentList[position]
        holder.itemView.apply {
            recipeName_tv.text = recipe.name
            recipeAuthor_tv.text = recipe.author
            //duration_tv.text = recipe.durationPrepare
            glide.load(recipe.imgUrl).into(imageView)

            setOnClickListener {
                onItemClickListener?.let { it(recipe) }
            }
        }
    }
}