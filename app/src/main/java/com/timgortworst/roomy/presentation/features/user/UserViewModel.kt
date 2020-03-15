package com.timgortworst.roomy.presentation.features.user

import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.ResponseState
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.base.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val userUseCase: UserUseCase
) : ViewModel() {
    private val _viewState = MutableLiveData<ResponseState>()
    val viewState: LiveData<ResponseState>
        get() = _viewState

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        val loadingJob = launch {
            delay(500)
            _viewState.postValue(ResponseState.Loading)
        }

        try {
            val userList = userUseCase.getAllUsersForHousehold()
            if (userList.isEmpty()) {
                _viewState.postValue(ResponseState.Error(R.string.empty_list_state_title_users))
            } else {
                _viewState.postValue(ResponseState.Success(userList))
            }
        } catch (e: FirebaseFirestoreException) {
            _viewState.postValue(ResponseState.Error(R.string.error_generic))
        }

        loadingJob.cancel()
    }

    suspend fun removeFromHousehold(user: User) = withContext(Dispatchers.IO) {
        userUseCase.deleteUser(user.userId)
    }

    fun displayBottomSheet(user: User) = liveData {
        val currentUser = userUseCase.getCurrentUser() ?: return@liveData
        if (currentUser.isAdmin && currentUser.userId != user.userId) emit(Event(Unit))
    }
}