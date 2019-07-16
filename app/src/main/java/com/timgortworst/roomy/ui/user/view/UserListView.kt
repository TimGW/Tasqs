package com.timgortworst.roomy.ui.user.view

import com.timgortworst.roomy.model.User

interface UserListView {
    fun presentUserList(users: MutableList<User>)
    fun showOrHideFab(condition: Boolean)
    fun refreshView(user: User)
    fun showContextMenuFor(user: User)
    fun share(householdId: String)
}
