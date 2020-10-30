package ua.co.myrecipes.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_recipetype.view.*
import ua.co.myrecipes.R
import ua.co.myrecipes.util.RecipeType
import javax.inject.Inject

class RecipeTypeAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseAdapter<RecipeType>(R.layout.item_recipetype) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val recipeType = differ.currentList[position]
        holder.itemView.apply {
            recipeType_tv.text = resources.getStringArray(R.array.recipeTypes)[position]
            glide.load(recipeType.img).into(recipeType_imv)
            setOnClickListener {
                onItemClickListener?.let { it(recipeType) }
            }
        }
    }
}