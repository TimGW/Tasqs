package com.timgortworst.roomy.presentation.base.model

data class BottomMenuItem(
    val resId: Int,
    val name: String,
    val action: () -> Unit
)