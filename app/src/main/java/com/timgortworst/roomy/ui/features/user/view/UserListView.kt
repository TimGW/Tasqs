package com.timgortworst.roomy.ui.features.user.view

import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.ui.base.view.PageStateView

interface UserListView : PageStateView {
//    fun showOrHideFab(condition: Boolean)
    fun showContextMenuFor(user: User)
    fun presentEditedUser(user: User)
    fun presentDeletedUser(user: User)
    fun presentAddedUser(user: User)
}
