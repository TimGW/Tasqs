package com.timgortworst.roomy.domain.model

data class BottomMenuItem(
    val resId: Int,
    val name: String,
    val action: () -> Unit
)