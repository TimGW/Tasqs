package com.timgortworst.tasqs.presentation.features.user

import androidx.lifecycle.*
import com.timgortworst.tasqs.domain.model.User
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.domain.usecase.user.GetUserUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.user.RemoveUserUseCaseImpl
import com.timgortworst.tasqs.presentation.usecase.user.GetAllUsersUseCase
import com.timgortworst.tasqs.presentation.usecase.user.GetUserUseCase
import com.timgortworst.tasqs.presentation.usecase.user.RemoveUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel(
    private val removeUserUseCase: RemoveUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel() {

    val allUsers = getAllUsersUseCase.execute(None).asLiveData(viewModelScope.coroutineContext)

    private val _userOptions = MutableLiveData<User>()
    val userOptions: LiveData<User>
        get() = _userOptions

    private val _userRemoved = MutableLiveData<Response<String>>()
    val userRemoved: LiveData<Response<String>>
        get() = _userRemoved

    fun removeFromHousehold(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            removeUserUseCase.execute(RemoveUserUseCaseImpl.Params(user.userId)).collect {
                _userRemoved.postValue(it)
            }
        }
    }

    fun shouldDisplayBottomSheetFor(user: User) {
        viewModelScope.launch(Dispatchers.IO) {

            getUserUseCase.execute(GetUserUseCaseImpl.Params()).collect { response ->
                if (response is Response.Success) {
                    if (response.data?.isAdmin == true &&
                        response.data.userId != user.userId
                    ) {
                        _userOptions.postValue(user)
                    }
                }
            }
        }
    }
}