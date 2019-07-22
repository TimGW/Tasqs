package com.timgortworst.roomy.ui.user.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.model.Role
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.user.view.UserListView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserListPresenter(
    private val view: UserListView,
    private val userRepository: UserRepository
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun fetchUsers() = scope.launch {
        val userList = userRepository
            .getUsersForHouseholdId(userRepository.getHouseholdIdForCurrentUser())
            .sortedWith(compareBy({ it.role == Role.ADMIN.name }, { it.name }))
            .reversed()
            .toMutableList()

        val currentUser = userList.find { it.userId == userRepository.getCurrentUserId() }
        view.showOrHideFab(userList.size < 8 && currentUser?.role == Role.ADMIN.name)
        view.presentUserList(userList)
    }

    fun deleteUser(user: User) = scope.launch {
        userRepository.deleteUser(user)
        view.removeUserFromCurrentUI(user)
    }

    fun showContextMenuIfUserHasPermission(user: User) = scope.launch {
        val currentUser = userRepository.getCurrentUser()
        if (user.role != Role.ADMIN.name &&
            currentUser.role == Role.ADMIN.name &&
            currentUser.userId != user.userId
        ) {
            view.showContextMenuFor(user)
        }
    }

    fun inviteUser() = scope.launch {
        view.share(userRepository.getHouseholdIdForCurrentUser())
    }
}
