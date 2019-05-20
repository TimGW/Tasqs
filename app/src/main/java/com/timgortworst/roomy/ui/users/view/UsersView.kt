package com.timgortworst.roomy.ui.users.view

import com.timgortworst.roomy.model.User

interface UsersView {
    fun presentUserList(users: MutableList<User>)
    fun presentCurrentUser(currentUser: User?)

}
