package com.timgortworst.roomy.ui.housemates.view

import com.timgortworst.roomy.model.User

interface HousenmatesView {
    fun presentUserList(users: MutableList<User>)
    fun presentCurrentUser(currentUser: User?)

}
