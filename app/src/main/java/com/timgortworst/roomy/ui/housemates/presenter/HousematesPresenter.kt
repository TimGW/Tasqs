package com.timgortworst.roomy.ui.housemates.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.Role
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.housemates.view.HousenmatesView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HousematesPresenter(
    private val view: HousenmatesView,
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
        val userList = userRepository
            .getUsersForHouseholdId(sharedPref.getActiveHouseholdId())
            .sortedWith(compareBy({ it.role == Role.ADMIN.name }, { it.name }))
            .reversed()
            .toMutableList()

        view.presentUserList(userList)
    }

    fun fetchCurrentUser() = scope.launch {
        val currentUser = userRepository.getOrCreateUser()
        view.presentCurrentUser(currentUser)
    }
}
