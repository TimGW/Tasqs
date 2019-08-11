package com.timgortworst.roomy.data.model

data class BottomMenuItem(
    val resId: Int,
    val name: String,
    val action: () -> Unit
)