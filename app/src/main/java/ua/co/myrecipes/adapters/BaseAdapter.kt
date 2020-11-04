package ua.co.myrecipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(
    private val layoutID: Int
): RecyclerView.Adapter<BaseAdapter.BaseViewHolder>() {

    class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BaseViewHolder(LayoutInflater.from(parent.context).inflate(layoutID, parent, false))

    protected var onItemClickListener: ((T) -> Unit)? = null

    fun setItemClickListener(listener: (T) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount() = items.size

    protected val diffCallback = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: T, newItem: T) =
            oldItem.hashCode() == newItem.hashCode()
    }

    protected abstract val differ: AsyncListDiffer<T>

    var items: MutableList<T>
        get() = differ.currentList
        set(value) = differ.submitList(value)
}