package com.timgortworst.roomy.presentation.features.user

import com.timgortworst.roomy.domain.model.User

interface OnLongClickListener {
    fun onLongClick(user: User) : Boolean
}