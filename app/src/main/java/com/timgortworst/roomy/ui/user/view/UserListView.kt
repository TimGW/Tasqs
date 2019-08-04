package com.timgortworst.roomy.ui.user.view

import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.User

interface UserListView {
    fun showOrHideFab(condition: Boolean)
    fun showContextMenuFor(user: User)
    fun presentEditedUser(user: User)
    fun presentDeletedUser(user: User)
    fun presentAddedUser(user: User)
    fun setLoadingView(isLoading: Boolean)
    fun setErrorView(isVisible: Boolean,
                     title: Int = R.string.error_list_state_title,
                     message: Int = R.string.error_list_state_text)
}
