package com.timgortworst.tasqs.presentation.features.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.usecase.task.CreateOrUpdateTaskUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.Event
import com.timgortworst.tasqs.presentation.usecase.task.CreateOrUpdateTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskEditViewModel(
    private val createOrUpdateTaskUseCase: CreateOrUpdateTaskUseCase
) : ViewModel() {

    private val _actionDone = MutableLiveData<Response<Task>>()
    val actionDone: LiveData<Response<Task>>
        get() = _actionDone

    private val _emptyDescMsg = MutableLiveData<Event<Int>>()
    val emptyDescMsg: LiveData<Event<Int>>
        get() = _emptyDescMsg

    fun taskDoneClicked(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        when {
            task.description.isEmpty() -> {
                _emptyDescMsg.postValue(Event(R.string.task_edit_error_empty_description))
            }
            else -> {
                val params = CreateOrUpdateTaskUseCaseImpl.Params(task)
                createOrUpdateTaskUseCase.execute(params).collect {
                    _actionDone.postValue(it)
                }
            }
        }
    }
}
