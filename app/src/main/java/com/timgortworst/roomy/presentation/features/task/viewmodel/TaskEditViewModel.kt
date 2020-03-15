package com.timgortworst.roomy.presentation.features.task.viewmodel

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.usecase.TaskUseCase
import com.timgortworst.roomy.domain.usecase.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime


class TaskEditViewModel(
    private val taskUseCase: TaskUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {
    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task>
        get() = _task

    fun fetchUsers() = liveData {
//        val userList = taskUseCase.getAllUsers()
//        val currentUser = userUseCase.getCurrentUser()

//        if (userList.filterNot { it.userId == currentUser?.userId }.isEmpty()) {
//            emit(currentUser)
//        } else {
//            emit(userList)
//        }
        emit(taskUseCase.getAllUsers())
    }


    suspend fun editTaskDone() = withContext(Dispatchers.IO) {
        task.value?.let { taskUseCase.createOrUpdateTask(it) }
    }

    fun setTaskDate(zonedDateTime: ZonedDateTime?) {
        zonedDateTime?.let { task.value?.metaData?.startDateTime = it }
//        viewModel.formatDate(task.metaData.startDateTime)
    }

    fun setTask(task: Task) {
        _task.value = task
    }
}