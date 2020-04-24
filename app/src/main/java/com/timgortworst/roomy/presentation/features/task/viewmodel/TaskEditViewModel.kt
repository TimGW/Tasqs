package com.timgortworst.roomy.presentation.features.task.viewmodel

import androidx.lifecycle.*
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.task.CreateOrUpdateTaskUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.Event
import com.timgortworst.roomy.presentation.usecase.task.CreateOrUpdateTaskUseCase
import com.timgortworst.roomy.presentation.usecase.user.GetAllUsersUseCase
import com.timgortworst.roomy.presentation.usecase.user.GetFbUserUseCase
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

    private val _prettyDate = MutableLiveData<Event<String>>()
    val prettyDate: LiveData<Event<String>>
        get() = _prettyDate

    private val _taskDone = MutableLiveData<Event<Response<Task>>>()
    val taskDone: LiveData<Event<Response<Task>>>
        get() = _taskDone

    fun formatDate(zonedDateTime: ZonedDateTime) {
        val formattedDayOfMonth = zonedDateTime.dayOfMonth.toString()
        val formattedMonth = zonedDateTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val formattedYear = zonedDateTime.year.toString()
        _prettyDate.value = Event("$formattedDayOfMonth $formattedMonth $formattedYear")
    }

    fun taskDoneClicked(task: Task) = viewModelScope.launch {
        if (task.description.isEmpty()) {
            _taskDone.value = Event(Response.Empty(R.string.task_edit_error_empty_description))
            return@launch
        }
        val params = CreateOrUpdateTaskUseCaseImpl.Params(task)
        createOrUpdateTaskUseCase.execute(params).collect {
            _taskDone.value = Event(it)
        }
    }
}
