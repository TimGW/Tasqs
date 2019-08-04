package com.timgortworst.roomy.ui.user.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.firestore.DocumentChange
import com.timgortworst.roomy.domain.UserListInteractor
import com.timgortworst.roomy.model.Role
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.BaseResponse
import com.timgortworst.roomy.ui.user.view.UserListView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserListPresenter @Inject constructor(
        private val view: UserListView,
        private val userListInteractor: UserListInteractor
) : BaseResponse(), DefaultLifecycleObserver {
    private val localUserList = mutableListOf<User>()
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
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

    override fun renderSuccessfulState(dc: List<DocumentChange>, totalDataSetSize: Int) {
        scope.launch {
            view.setLoadingView(false)
            view.setErrorView(false)
            for (docChange in dc) {
                val user = docChange.document.toObject(User::class.java)
                when (docChange.type) {
                    DocumentChange.Type.ADDED -> {
                        view.presentAddedUser(user)

                        localUserList.add(user)
                        view.showOrHideFab(localUserList.size < 8 &&
                                userListInteractor.getCurrentUser()?.role == Role.ADMIN.name)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        localUserList[localUserList.indexOf(user)] = user
                        view.presentEditedUser(user)
                    }
                    DocumentChange.Type.REMOVED -> {
                        localUserList.remove(user)
                        view.presentDeletedUser(user)
                    }
                }
            }
        }
    }

    override fun renderLoadingState() {
        view.setLoadingView(true)
    }

    override fun renderUnsuccessfulState(throwable: Throwable) {
        view.setLoadingView(false)
        view.setErrorView(true)
    }
}
