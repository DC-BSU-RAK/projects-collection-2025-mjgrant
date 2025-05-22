package com.example.bubba

import androidx.recyclerview.widget.DiffUtil

class CardStackCallback(
    private val oldList: List<BlendCard>,
    private val newList: List<BlendCard>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Assuming imageResId and title combo uniquely identifies a card
        return oldList[oldItemPosition].imageResId == newList[newItemPosition].imageResId &&
                oldList[oldItemPosition].title == newList[newItemPosition].title
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
