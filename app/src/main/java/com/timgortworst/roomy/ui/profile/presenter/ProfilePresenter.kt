package com.timgortworst.roomy.ui.profile.presenter

import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.profile.view.ProfileView
import kotlinx.coroutines.InternalCoroutinesApi



class ProfilePresenter(
    private val view: ProfileView,
    private val userRepository: UserRepository
) {

    fun getCurrentUser() {
        userRepository.getUser { user ->
            view.presentUser(user!!)
        }
    }
}
