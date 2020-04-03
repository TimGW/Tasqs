package com.timgortworst.roomy.presentation.features.user

import androidx.lifecycle.*
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.firestore.User
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.domain.utils.snackbar
import com.timgortworst.roomy.presentation.base.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val userUseCase: UserUseCase
) : ViewModel() {

    val allUsersLiveData: LiveData<Response<List<User>>> = userUseCase.getAllUsersForHousehold()

    private val _userOptions = MutableLiveData<Event<User>>()
    val userOptions: LiveData<Event<User>>
        get() = _userOptions

    private val _removedUser = MutableLiveData<Response<User>>()
    val removedUser: LiveData<Response<User>>
        get() = _removedUser

    fun removeFromHousehold(user: User) {
        viewModelScope.launch {
             userUseCase.removeUserFromHousehold(user).collect {
                 _removedUser.value = it
             }
        }
    }

    fun shouldDisplayBottomSheetFor(user: User) {
        viewModelScope.launch {
            val currentUser = userUseCase.getCurrentUser() ?: return@launch
            if (currentUser.isAdmin && currentUser.userId != user.userId) {
                _userOptions.value = Event(user)
            }
        }
    }
}