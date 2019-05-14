package com.timgortworst.roomy.ui.users.presenter

import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.users.view.UsersView
import kotlinx.coroutines.InternalCoroutinesApi



class UsersPresenter(
        private val view: UsersView,
        private val userRepository: UserRepository
) {

    fun fetchUsers() {
        userRepository.getUsersForHousehold(object : UserRepository.UserListener {
            override fun provideUserList(users: MutableList<User>) {
                view.presentUserList(users)
            }
        })
    }

}
