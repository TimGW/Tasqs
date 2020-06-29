package com.timgortworst.tasqs.presentation.features.user

import com.timgortworst.tasqs.domain.model.User

interface OnLongClickListener {
    fun onLongClick(user: User) : Boolean
}