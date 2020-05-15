package com.timgortworst.roomy.presentation.features.task.viewmodel

import androidx.lifecycle.*
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.domain.model.TaskUser
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.task.CreateOrUpdateTaskUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.Event
import com.timgortworst.roomy.presentation.usecase.task.CreateOrUpdateTaskUseCase
import com.timgortworst.roomy.presentation.usecase.user.GetAllUsersUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.*

class TaskEditViewModel(
    private val createOrUpdateTaskUseCase: CreateOrUpdateTaskUseCase,
    getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel() {
    val allUsersLiveData = getAllUsersUseCase.execute().asLiveData(viewModelScope.coroutineContext)

    private val _actionDone = MutableLiveData<Event<Response<Task>>>()
    val actionDone: LiveData<Event<Response<Task>>>
        get() = _actionDone

    fun taskDoneClicked(task: Task) = viewModelScope.launch {
        if (task.description.isEmpty()) {
            _actionDone.value = Event(Response.Empty(R.string.task_edit_error_empty_description))
            return@launch
        }
        val params = CreateOrUpdateTaskUseCaseImpl.Params(task)
        createOrUpdateTaskUseCase.execute(params).collect {
            _actionDone.value = Event(it)
        }
    }
}
