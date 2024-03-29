package com.timgortworst.tasqs.presentation.features.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.domain.usecase.task.CompleteTaskUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.task.DeleteTaskUseCaseImpl
import com.timgortworst.tasqs.presentation.usecase.task.CompleteTaskUseCase
import com.timgortworst.tasqs.presentation.usecase.task.DeleteTaskUseCase
import com.timgortworst.tasqs.presentation.usecase.task.GetAllTasksUseCase
import com.timgortworst.tasqs.presentation.usecase.task.GetTasksForUserQueryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskListViewModel(
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getTasksForUserQueryUseCase: GetTasksForUserQueryUseCase
) : ViewModel() {

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    private val _liveQueryOptions = MutableLiveData<FirestoreRecyclerOptions.Builder<Task>>()
    val liveQueryOptions: LiveData<FirestoreRecyclerOptions.Builder<Task>>
        get() = _liveQueryOptions

    fun tasksCompleted(tasks: List<Task>): Flow<Response<Nothing>> {
        return completeTaskUseCase.execute(CompleteTaskUseCaseImpl.Params(tasks))
    }

    fun deleteTasks(tasks: List<Task>): Flow<Response<Nothing>> {
        return deleteTaskUseCase.execute(DeleteTaskUseCaseImpl.Params(tasks))
    }

    suspend fun loadInitialQuery() = withContext(Dispatchers.IO) {
        if (_liveQueryOptions.value != null) return@withContext

        val loadingJob = launch {
            delay(500)
            _showLoading.postValue(true)
        }

        allDataQuery()

        loadingJob.cancel()
        _showLoading.postValue(false)
    }

    suspend fun allDataQuery() = withContext(Dispatchers.IO) {
        val fireStoreOptions = getAllTasksUseCase.execute(None)
        _liveQueryOptions.postValue(fireStoreOptions)
    }

    suspend fun filterDataQuery() = withContext(Dispatchers.IO) {
        val fireStoreOptions = getTasksForUserQueryUseCase.execute(None)
        _liveQueryOptions.postValue(fireStoreOptions)
    }
}