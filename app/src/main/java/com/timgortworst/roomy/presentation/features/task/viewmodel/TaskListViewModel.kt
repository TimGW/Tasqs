package com.timgortworst.roomy.presentation.features.task.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
    private val taskUseCase: TaskUseCase,
    application: Application
) : AndroidViewModel(application) {

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private val _data = MutableLiveData<FirestoreRecyclerOptions.Builder<Task>>()
    val data: LiveData<FirestoreRecyclerOptions.Builder<Task>> = _data

    init {
        loadData()
    }

    suspend fun tasksCompleted(tasks: List<Task>) = withContext(Dispatchers.IO) {
        taskUseCase.tasksCompleted(tasks)
    }

    suspend fun deleteTasks(tasks: List<Task>) = withContext(Dispatchers.IO) {
        taskUseCase.deleteTasks(tasks)
    }

    private fun loadData() = viewModelScope.launch {
        val loadingJob = launch {
            delay(500)
            _showLoading.value = true
        }

        _data.value = FirestoreRecyclerOptions.Builder<Task>()
            .setQuery(taskUseCase.getAllTasksQuery()) {
                CustomMapper.toTask(it.toObject(TaskJson::class.java)!!)!!
            }
        loadingJob.cancel()
        _showLoading.value = false
    }
}