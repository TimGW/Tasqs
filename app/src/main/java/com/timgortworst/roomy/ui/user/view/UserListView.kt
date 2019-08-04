package com.timgortworst.roomy.ui.user.view

import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.ui.main.view.PageStateListener

interface UserListView : PageStateListener {
    fun showOrHideFab(condition: Boolean)
    fun showContextMenuFor(user: User)
    fun presentEditedUser(user: User)
    fun presentDeletedUser(user: User)
    fun presentAddedUser(user: User)
}
