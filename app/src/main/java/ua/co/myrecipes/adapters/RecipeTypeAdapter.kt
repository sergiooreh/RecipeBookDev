package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_recipetype.view.*
import ua.co.myrecipes.R
import ua.co.myrecipes.util.RecipeType

class RecipeTypeAdapter : RecyclerView.Adapter<RecipeTypeAdapter.MyViewHolder>()  {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_recipetype, parent, false))

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val recipeType = differ.currentList[position]
        holder.itemView.apply {
            recipeType_tv.text = recipeType.toString()
            recipeType_imv.setImageResource(recipeType.img)
            setOnClickListener {
                onItemClickListener?.let { it(recipeType) }
            }
        }
    }

    private var onItemClickListener: ((RecipeType) -> Unit)? = null

    fun setOnItemClickListener(listener: ((RecipeType) -> Unit)) { onItemClickListener = listener }

    private val differCallback = object : DiffUtil.ItemCallback<RecipeType>() {
        override fun areItemsTheSame(oldItem: RecipeType, newItem: RecipeType): Boolean {
            return oldItem.name == newItem.name     //we could check by id, but we use api articles, there can be different id
        }
        override fun areContentsTheSame(oldItem: RecipeType, newItem: RecipeType): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,differCallback)       //takes two our list and compares them(it's ASYNC)
}