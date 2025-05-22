package com.example.bubba

data class BlendCard(
    val imageResId: Int,     // Drawable resource ID (e.g., R.drawable.taro)
    val title: String,       // Name of the item (e.g., "Taro")
    val category: String     // Category (e.g., "Flavor", "Size", "Sugar", etc.)
)
