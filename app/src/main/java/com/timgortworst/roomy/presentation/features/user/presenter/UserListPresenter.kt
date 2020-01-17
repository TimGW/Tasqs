package com.timgortworst.roomy.presentation.features.user.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.firestore.DocumentChange
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Role
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.domain.ApiStatus
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.user.view.UserListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserListPresenter @Inject constructor(
        private val view: UserListView,
        private val userUseCase: UserUseCase
) : ApiStatus(), DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun detachUserListener() {
        userUseCase.detachUserListener()
    }

    fun listenToUsers() = scope.launch {
        userUseCase.listenToUsers(this@UserListPresenter)
    }

    fun deleteUser(user: User) = scope.launch {
        userUseCase.deleteAndBanUser(user)
    }

    fun showContextMenuIfUserHasPermission(user: User) = scope.launch {
        val currentUser = userUseCase.getCurrentUser() ?: return@launch

        if (user.role != Role.ADMIN.name &&
                currentUser.role == Role.ADMIN.name &&
                currentUser.userId != user.userId
        ) {
            view.showContextMenuFor(user)
        }
    }

    override fun renderSuccessfulState(dc: List<Pair<*, DocumentChange.Type>>, totalDataSetSize: Int, hasPendingWrites: Boolean) {
        scope.launch {
            view.setLoadingView(false)
            view.setErrorView(false)

            dc.forEach {
                val user = (it.first as? User) ?: return@launch

                when (it.second) {
                    DocumentChange.Type.ADDED -> view.presentAddedUser(user)
                    DocumentChange.Type.MODIFIED -> view.presentEditedUser(user)
                    DocumentChange.Type.REMOVED -> view.presentDeletedUser(user)
                }
            }
        }
    }

    override fun renderLoadingState() {
        view.setLoadingView(true)
    }

    override fun renderUnsuccessfulState(throwable: Throwable) {
        view.setLoadingView(false)
        view.setErrorView(true, R.string.error_list_state_title, R.string.error_list_state_text)
    }
}
