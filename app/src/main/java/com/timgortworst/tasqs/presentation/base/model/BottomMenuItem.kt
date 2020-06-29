package com.timgortworst.tasqs.presentation.base.model

data class BottomMenuItem(
    val resId: Int,
    val name: String,
    val action: () -> Unit
)