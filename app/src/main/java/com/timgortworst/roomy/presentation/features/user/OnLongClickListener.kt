package com.timgortworst.roomy.presentation.features.user

import com.timgortworst.roomy.domain.model.firestore.User

interface OnLongClickListener {
    fun onLongClick(user: User) : Boolean
}