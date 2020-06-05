package com.timgortworst.roomy.presentation.features.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.task.CompleteTaskUseCaseImpl
import com.timgortworst.roomy.domain.usecase.task.GetTaskUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.Event
import com.timgortworst.roomy.presentation.base.model.TaskInfoAction
import com.timgortworst.roomy.presentation.usecase.task.CompleteTaskUseCase
import com.timgortworst.roomy.presentation.usecase.task.GetTaskUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskInfoViewModel(
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : ViewModel() {

    private val _taskInfoAction = MutableLiveData<Event<TaskInfoAction>>()
    val taskInfoAction: LiveData<Event<TaskInfoAction>>
        get() = _taskInfoAction

    private val _task = MutableLiveData<Response<Task>>()
    val task: LiveData<Response<Task>>
        get() = _task

    fun getTaskOrNull(): Task? {
        return when (val taskValue = task.value) {
            is Response.Success -> taskValue.data
            else -> null
        }
    }

    fun taskCompleted(task: Task) {
        viewModelScope.launch {
            completeTaskUseCase.execute(CompleteTaskUseCaseImpl.Params(listOf(task))).collect()
            _taskInfoAction.value = Event(TaskInfoAction.Continue)
        }
    }

    fun fetchTask(id: String) {
        viewModelScope.launch {
            getTaskUseCase.execute(GetTaskUseCaseImpl.Params(id)).collect {
                _task.value = it
            }
        }
    }

    fun setTaskFromLocalSource(task: Task) {
        _task.value = Response.Success(task)
    }
}