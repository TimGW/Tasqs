package com.timgortworst.roomy.presentation.features.user

import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.presentation.base.view.PageStateView

interface UserListView : PageStateView {
    fun showContextMenuFor(user: User)
    fun presentEditedUser(user: User)
    fun presentDeletedUser(user: User)
    fun presentAddedUser(user: User)
}
