package com.example.bubba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class BlendAdapter(
    private val blendList: MutableList<BlendData>,
    private val onFavoriteClicked: (BlendData) -> Unit
) : RecyclerView.Adapter<BlendAdapter.BlendViewHolder>() {

    class BlendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.blendSummaryTitle)
        val image: ImageView = itemView.findViewById(R.id.summaryImage)
        val summary: TextView = itemView.findViewById(R.id.summaryText)
        val favoriteButton: ImageView = itemView.findViewById(R.id.favoriteButton)
        val removeButton: ImageView = itemView.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blend_card, parent, false)
        return BlendViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlendViewHolder, position: Int) {
        val blend = blendList[position]

        holder.title.text = "Blend Summary"
        holder.image.setImageResource(blend.imageResId)

        val summaryText = buildString {
            append("Flavor: ${blend.flavor}\n")
            append("Size: ${blend.size}\n")
            append("Sugar: ${blend.sugar}\n")
            append("Ice: ${blend.ice}\n")
            append("Add-ons: ${if (blend.addOns.isNotEmpty()) blend.addOns.joinToString(", ") else "None"}")
        }
        holder.summary.text = summaryText

        // Favorite logic
        holder.favoriteButton.setOnClickListener {
            onFavoriteClicked(blend)
            Toast.makeText(holder.itemView.context, "Marked as favorite!", Toast.LENGTH_SHORT).show()
        }

        // Remove logic
        holder.removeButton.setOnClickListener {
            blendList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, blendList.size)
        }
    }

    override fun getItemCount(): Int = blendList.size
}
