package com.timgortworst.roomy.presentation.features.user

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.user.RemoveUserUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.Event
import com.timgortworst.roomy.presentation.usecase.GetAllUsersUseCase
import com.timgortworst.roomy.presentation.usecase.GetUserUseCase
import com.timgortworst.roomy.presentation.usecase.RemoveUserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel(
    private val removeUserUseCase: RemoveUserUseCase,
    getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel() {

    val allUsers = getAllUsersUseCase.execute()

    private val _userOptions = MutableLiveData<Event<User>>()
    val userOptions: LiveData<Event<User>>
        get() = _userOptions

    private val _userRemoved = MutableLiveData<Response<String>>()
    val userRemoved: LiveData<Response<String>>
        get() = _userRemoved

    fun removeFromHousehold(user: User) {
        viewModelScope.launch {
            removeUserUseCase.execute(RemoveUserUseCaseImpl.Params(user.userId)).collect {
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