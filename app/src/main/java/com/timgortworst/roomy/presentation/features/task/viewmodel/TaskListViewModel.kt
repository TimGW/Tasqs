package com.timgortworst.roomy.presentation.features.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.timgortworst.roomy.data.utils.CustomMapper
import com.timgortworst.roomy.domain.entity.response.Response
import com.timgortworst.roomy.domain.entity.Task
import com.timgortworst.roomy.domain.entity.firestore.TaskJson
import com.timgortworst.roomy.domain.usecase.TaskListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskListViewModel(
    private val taskUseCase: TaskListUseCase
) : ViewModel() {

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    private val _liveQueryOptions = MutableLiveData<FirestoreRecyclerOptions.Builder<Task>>()
    val liveQueryOptions: LiveData<FirestoreRecyclerOptions.Builder<Task>>
        get() = _liveQueryOptions

    fun tasksCompleted(tasks: List<Task>): Flow<Response<Nothing>> {
        return taskUseCase.tasksCompleted(tasks)
    }

    fun deleteTasks(tasks: List<Task>): Flow<Response<Nothing>> {
        return taskUseCase.deleteTasks(tasks)
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
        _liveQueryOptions.postValue(FirestoreRecyclerOptions.Builder<Task>()
            .setQuery(taskUseCase.getAllTasksQuery()) {
                CustomMapper.toTask(it.toObject(TaskJson::class.java)!!)!!
            })
    }

    suspend fun filterDataQuery() = withContext(Dispatchers.IO) {
        _liveQueryOptions.postValue(FirestoreRecyclerOptions.Builder<Task>()
            .setQuery(taskUseCase.getTasksForUserQuery()) {
                CustomMapper.toTask(it.toObject(TaskJson::class.java)!!)!!
            })
    }
}