package com.timgortworst.roomy.presentation.features.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.timgortworst.roomy.domain.model.Role
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.usecase.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserViewModel(
    private val userUseCase: UserUseCase
) : ViewModel() {

    suspend fun deleteUser(user: User) = withContext(Dispatchers.IO) {
        userUseCase.deleteAndBanUser(user)
    }

    fun fetchFireStoreRecyclerOptionsBuilder() = liveData {
        val options : FirestoreRecyclerOptions.Builder<User> = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(userUseCase.getUsersForHousehold(), User::class.java)

        emit(options)
    }

    fun userAdminObservable(user: User): LiveData<User?> = liveData {
        val result = userUseCase.getCurrentUser()

        if (user.role != Role.ADMIN.name &&
            result?.role == Role.ADMIN.name &&
            result.userId != user.userId
        ) {
            emit(user)
        } else {
            emit(null)
        }
    }
}