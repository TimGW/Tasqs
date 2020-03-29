package com.timgortworst.roomy.presentation.features.user

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.Response
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.base.Event

class UserViewModel(
    private val userUseCase: UserUseCase
) : ViewModel() {

    var viewState: LiveData<Response<List<User>>> = userUseCase
        .getAllUsersForHousehold()
        .asLiveData(viewModelScope.coroutineContext)

    suspend fun removeFromHousehold(user: User) {
        userUseCase.removeUserFromHousehold(user.userId)
    }

    fun displayBottomSheet(user: User) = liveData {
        val currentUser = userUseCase.getCurrentUser() ?: return@liveData
        if (currentUser.isAdmin && currentUser.userId != user.userId) emit(Event(Unit))
    }
}