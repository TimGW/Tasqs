package com.timgortworst.roomy.presentation.features.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.task.Task
import com.timgortworst.roomy.domain.usecase.TaskUseCase
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.base.Event
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.*

class TaskEditViewModel(
    private val taskUseCase: TaskUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {
    val allUsersLiveData = userUseCase.getAllUsersForHousehold()

    private val _prettyDate = MutableLiveData<Event<String>>()
    val prettyDate: LiveData<Event<String>>
        get() = _prettyDate

    private val _taskDone = MutableLiveData<Event<Response<Task>>>()
    val taskDone: LiveData<Event<Response<Task>>>
        get() = _taskDone


    suspend fun currentUser() = userUseCase.getCurrentUser()

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
        taskUseCase.createOrUpdateTask(task).collect {
            _taskDone.value = Event(it)
        }
    }
}
