package ua.co.myrecipes.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import kotlinx.android.synthetic.main.item_direction.view.*
import ua.co.myrecipes.R

class DirectionsAdapter(
    var directions: MutableList<String>
): BaseAdapter<String>(R.layout.item_direction) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.itemView.direction_name_tv.text = directions[position]
    }
}