package com.timgortworst.roomy.presentation.features.user

import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.Response
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
    private val _viewState = MutableLiveData<Response<List<User>>>()
    val viewState: LiveData<Response<List<User>>>
        get() = _viewState

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        val loadingJob = launch {
            delay(500)
            _viewState.postValue(Response.Loading)
        }

        try {
            _viewState.postValue(Response.Success(userUseCase.getAllUsersForHousehold()))
        } catch (e: FirebaseFirestoreException) {
            _viewState.postValue(Response.Error(e))
        }

        loadingJob.cancel()
    }

    suspend fun removeFromHousehold(user: User) = withContext(Dispatchers.IO) {
        userUseCase.removeUserFromHousehold(user.userId)
    }

    fun displayBottomSheet(user: User) = liveData {
        val currentUser = userUseCase.getCurrentUser() ?: return@liveData
        if (currentUser.isAdmin && currentUser.userId != user.userId) emit(Event(Unit))
    }
}