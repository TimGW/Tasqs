package com.timgortworst.roomy.ui.profile.view

import com.timgortworst.roomy.model.User

interface ProfileView {
    fun presentUser(user: User)
    fun presentToastError(generic_error: Int)
    fun restartApplication()
    fun revokeUserAccess()
}
