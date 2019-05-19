package com.timgortworst.roomy.ui.users.presenter

import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.users.view.UsersView


class UsersPresenter(
    private val view: UsersView,
    private val userRepository: UserRepository,
    private val sharedPref: HuishoudGenootSharedPref
) {

    fun fetchUsers() {
        userRepository.getUsersForHouseholdId(sharedPref.getActiveHouseholdId(),
            onComplete = {
                view.presentUserList(it.toMutableList())
            },
            onFailure = { })

    }
}
