package com.timgortworst.roomy.ui.profile.view

import com.timgortworst.roomy.model.User

interface ProfileView {
    fun presentUser(user: User)

}
