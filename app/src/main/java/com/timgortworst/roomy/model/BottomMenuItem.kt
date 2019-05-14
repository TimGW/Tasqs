package com.timgortworst.roomy.model

data class BottomMenuItem(
    val resId: Int,
    val name: String,
    val action: () -> Unit
)