package com.timgortworst.roomy.presentation.features.user

import com.timgortworst.roomy.domain.entity.User

interface OnLongClickListener {
    fun onLongClick(user: User) : Boolean
}