package com.timgortworst.roomy.ui.user.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.domain.UserListInteractor
import com.timgortworst.roomy.model.Role
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.user.view.UserListView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserListPresenter @Inject constructor(
        private val view: UserListView,
        private val userListInteractor: UserListInteractor
) : UserRepository.UserListener, DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    override fun userAdded(user: User) {
        view.presentAddedUser(user)
    }

    override fun userModified(user: User) {
        view.presentEditedUser(user)
    }

    override fun userDeleted(user: User) {
        view.presentDeletedUser(user)
    }

    override fun setLoading(isLoading: Boolean) {
        //view.setLoading(isLoading)
    }

    fun detachUserListener() {
        userListInteractor.detachUserListener()
    }

    fun listenToUsers() = scope.launch {
        userListInteractor.listenToUsers(this@UserListPresenter)
    }

    fun deleteUser(user: User) = scope.launch {
        userListInteractor.deleteAndBanUser(user)
    }

    fun showContextMenuIfUserHasPermission(user: User) = scope.launch {
        val currentUser = userListInteractor.getCurrentUser() ?: return@launch

        if (user.role != Role.ADMIN.name &&
                currentUser.role == Role.ADMIN.name &&
                currentUser.userId != user.userId
        ) {
            view.showContextMenuFor(user)
        }
    }

    fun inviteUser() = scope.launch {
        view.share(userListInteractor.getHouseholdIdForCurrentUser())
    }
}
