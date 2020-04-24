package com.timgortworst.roomy.presentation.features.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.task.CompleteTaskUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.Event
import com.timgortworst.roomy.presentation.base.model.TaskInfoAction
import com.timgortworst.roomy.presentation.usecase.task.CompleteTaskUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskInfoViewModel(
    private val completeTaskUseCase: CompleteTaskUseCase
) : ViewModel() {

    private val _taskInfoAction = MutableLiveData<Event<TaskInfoAction>>()
    val taskInfoAction: LiveData<Event<TaskInfoAction>>
        get() = _taskInfoAction

    fun taskCompleted(task: Task) {
        _taskInfoAction.value = Event(TaskInfoAction.Continue)

        viewModelScope.launch {
            completeTaskUseCase.execute(CompleteTaskUseCaseImpl.Params(listOf(task))).collect()
        }
    }
}