package com.timgortworst.roomy.ui.users.presenter

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.users.view.UsersView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UsersPresenter(
    private val view: UsersView,
    private val userRepository: UserRepository,
    private val sharedPref: HuishoudGenootSharedPref
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun fetchUsers() = scope.launch {
        val userList = userRepository.getUsersForHouseholdId(sharedPref.getActiveHouseholdId())
        view.presentUserList(userList.toMutableList())
    }

    fun fetchCurrentUser() = scope.launch {
        val currentUser = userRepository.getOrCreateUser()
        view.presentCurrentUser(currentUser)
    }
}
