package com.timgortworst.roomy.presentation.features.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.timgortworst.roomy.data.repository.CustomMapper
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.domain.usecase.TaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskListViewModel(
    private val taskUseCase: TaskUseCase
) : ViewModel() {

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private val _liveQueryOptions = MutableLiveData<FirestoreRecyclerOptions.Builder<Task>>()
    val liveQueryOptions: LiveData<FirestoreRecyclerOptions.Builder<Task>> = _liveQueryOptions

    suspend fun tasksCompleted(tasks: List<Task>) = withContext(Dispatchers.IO) {
        taskUseCase.tasksCompleted(tasks)
    }

    suspend fun deleteTasks(tasks: List<Task>) = withContext(Dispatchers.IO) {
        taskUseCase.deleteTasks(tasks)
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