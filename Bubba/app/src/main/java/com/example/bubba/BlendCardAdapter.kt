package com.example.bubba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class BlendCardAdapter(
    private var items: List<BlendCard>
) : RecyclerView.Adapter<BlendCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    /**
     * Call this when changing the list with new data using DiffUtil for smoother UI transitions.
     */
    fun updateItems(newItems: List<BlendCard>) {
        val diffCallback = CardStackCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItems(): List<BlendCard> = items

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.item_image)
        private val title: TextView = itemView.findViewById(R.id.item_title)
        private val category: TextView = itemView.findViewById(R.id.item_category)

        fun bind(card: BlendCard) {
            image.setImageResource(card.imageResId)
            title.text = card.title
            category.text = card.category
        }
    }
}
