package com.timgortworst.roomy.ui.housemates.view

import com.timgortworst.roomy.model.User

interface UserListView {
    fun presentUserList(users: MutableList<User>)
    fun showOrHideFab(condition: Boolean)
    fun refreshView(user: User)
    fun showContextMenuFor(user: User)
}
