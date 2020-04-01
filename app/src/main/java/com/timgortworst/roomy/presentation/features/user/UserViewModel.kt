package com.timgortworst.roomy.presentation.features.user

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.firestore.User
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.base.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val userUseCase: UserUseCase
) : ViewModel() {

    private var _viewState = MutableLiveData<Response<List<User>>>()
    val viewState: LiveData<Response<List<User>>>
        get() = _viewState

    init {
        getUsers()
    }

    private fun getUsers() {
        viewModelScope.launch {
            userUseCase.getAllUsersForHousehold().collect {
                _viewState.value = it
            }
        }
    }

    suspend fun removeFromHousehold(user: User) = withContext(Dispatchers.IO) {
        userUseCase.removeUserFromHousehold(user.userId).collect()
    }

    fun displayBottomSheet(user: User) = liveData {
        val currentUser = userUseCase.getCurrentUser() ?: return@liveData
        if (currentUser.isAdmin && currentUser.userId != user.userId) emit(Event(Unit))
    }
}