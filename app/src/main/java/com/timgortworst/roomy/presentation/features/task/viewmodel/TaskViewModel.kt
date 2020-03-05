package com.timgortworst.roomy.presentation.features.task.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.timgortworst.roomy.data.repository.CustomMapper
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.domain.usecase.TaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskViewModel(private val taskUseCase: TaskUseCase) : ViewModel() {

    suspend fun tasksCompleted(tasks: List<Task>) = withContext(Dispatchers.IO) {
        taskUseCase.tasksCompleted(tasks)
    }

    suspend fun deleteTasks(tasks: List<Task>) = withContext(Dispatchers.IO) {
        taskUseCase.deleteTasks(tasks)
    }

    fun fetchFireStoreRecyclerOptionsBuilder() = liveData {
        val options = FirestoreRecyclerOptions.Builder<Task>()
            .setQuery(taskUseCase.getAllTasksQuery()) {
                CustomMapper.toTask(it.toObject(TaskJson::class.java)!!)!!
            }
        emit(options)
    }
}