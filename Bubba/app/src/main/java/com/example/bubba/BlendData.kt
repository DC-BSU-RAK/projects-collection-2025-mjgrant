package com.example.bubba

data class BlendData(
    val flavor: String,
    val size: String,
    val sugar: String,
    val ice: String,
    val addOns: List<String> = listOf(),
    val imageResId: Int // drawable resource ID of blend image
)
