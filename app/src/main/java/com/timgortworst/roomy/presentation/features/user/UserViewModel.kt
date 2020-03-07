package com.timgortworst.roomy.presentation.features.user

import androidx.lifecycle.*
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.timgortworst.roomy.data.repository.CustomMapper
import com.timgortworst.roomy.domain.model.Role
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.domain.usecase.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val userUseCase: UserUseCase
) : ViewModel() {
    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private val _data = MutableLiveData<FirestoreRecyclerOptions.Builder<User>>()
    val data: LiveData<FirestoreRecyclerOptions.Builder<User>> = _data

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        val loadingJob = launch {
            delay(500)
            _showLoading.value = true
        }

        _data.value = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(userUseCase.getAllUsersQuery(), User::class.java)
        loadingJob.cancel()
        _showLoading.value = false
    }
}