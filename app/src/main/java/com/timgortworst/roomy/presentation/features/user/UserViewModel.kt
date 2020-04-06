package com.timgortworst.roomy.presentation.features.user

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.entity.response.Response
import com.timgortworst.roomy.domain.entity.User
import com.timgortworst.roomy.domain.usecase.GetAllUsersUseCase
import com.timgortworst.roomy.domain.usecase.RemoveUserUseCase
import com.timgortworst.roomy.presentation.base.model.Event
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel(
    private val removeUserUseCase: RemoveUserUseCase,
    getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel() {

    val allUsers = getAllUsersUseCase.executeUseCase()

    private val _userOptions = MutableLiveData<Event<User>>()
    val userOptions: LiveData<Event<User>>
        get() = _userOptions

    private val _userRemoved = MutableLiveData<Response<String>>()
    val userRemoved: LiveData<Response<String>>
        get() = _userRemoved

    fun removeFromHousehold(user: User) {
        viewModelScope.launch {
            removeUserUseCase.init(user.userId).executeUseCase().collect {
                _userRemoved.value = it
            }
        }
    }

    fun shouldDisplayBottomSheetFor(user: User) {
        viewModelScope.launch {
            if (user.isAdmin && user.userId != user.userId) {
                _userOptions.value = Event(user)
            }
        }
    }
}