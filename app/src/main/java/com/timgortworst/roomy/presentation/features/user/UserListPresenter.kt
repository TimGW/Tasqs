package com.timgortworst.roomy.presentation.features.user

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.firestore.DocumentChange
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Role
import com.timgortworst.roomy.domain.model.UIState
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserListPresenter(
        private val view: UserListView,
        private val userUseCase: UserUseCase
) : UIState<User>, DefaultLifecycleObserver {
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

    override fun renderSuccessfulState(changeSet: List<Pair<User, DocumentChange.Type>>,
                                       totalDataSetSize: Int,
                                       hasPendingWrites: Boolean) {
        scope.launch {
            changeSet.filterNot {
                it.first.userId == userUseCase.getCurrentUser()?.userId
            }.also {
                view.setMsgView(it.isEmpty(), R.string.empty_list_state_title_users, R.string.empty_list_state_text_users)
            }.forEach {
                when (it.second) {
                    DocumentChange.Type.ADDED -> view.presentAddedUser(it.first)
                    DocumentChange.Type.MODIFIED -> view.presentEditedUser(it.first)
                    DocumentChange.Type.REMOVED -> view.presentDeletedUser(it.first)
                }
            }
        }
    }

    override fun renderLoadingState(isLoading: Boolean) {
        view.setLoadingView(isLoading)
    }

    override fun renderErrorState(hasError: Boolean) {
        view.setMsgView(hasError, R.string.error_list_state_title, R.string.error_list_state_text)
    }
}
