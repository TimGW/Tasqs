package com.timgortworst.tasqs.presentation.features.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.usecase.task.CompleteTaskUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.task.DeleteTaskUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.task.GetTaskUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.TaskInfoAction
import com.timgortworst.tasqs.presentation.usecase.task.CompleteTaskUseCase
import com.timgortworst.tasqs.presentation.usecase.task.DeleteTaskUseCase
import com.timgortworst.tasqs.presentation.usecase.task.GetTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskInfoViewModel(
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : ViewModel() {

    private val _taskInfoAction = MutableLiveData<TaskInfoAction>()
    val taskInfoAction: LiveData<TaskInfoAction>
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
        _taskInfoAction.value = TaskInfoAction.Loading

        viewModelScope.launch(Dispatchers.IO) {
            completeTaskUseCase.execute(CompleteTaskUseCaseImpl.Params(listOf(task))).collect {
                _taskInfoAction.postValue(TaskInfoAction.Finish)
            }
        }
    }

    fun fetchTask(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getTaskUseCase.execute(GetTaskUseCaseImpl.Params(id)).collect {
                _task.postValue(it)
            }
        }
    }

    fun setTaskFromLocalSource(task: Task) {
        _task.value = Response.Success(task)
    }

    fun deleteTask() {
        _taskInfoAction.value = TaskInfoAction.Loading

        viewModelScope.launch(Dispatchers.IO) {
            deleteTaskUseCase.execute(
                DeleteTaskUseCaseImpl.Params((listOf(task.value as? Response.Success).map {
                    it?.data ?: return@launch
                }))
            ).collect {
                _taskInfoAction.postValue(TaskInfoAction.Finish)
            }
        }
    }
}