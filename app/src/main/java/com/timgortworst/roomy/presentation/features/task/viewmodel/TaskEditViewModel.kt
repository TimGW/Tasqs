package com.timgortworst.roomy.presentation.features.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.entity.response.Response
import com.timgortworst.roomy.domain.entity.Task
import com.timgortworst.roomy.domain.usecase.GetAllUsersUseCase
import com.timgortworst.roomy.domain.usecase.GetUserUseCase
import com.timgortworst.roomy.domain.usecase.TaskEditUseCase
import com.timgortworst.roomy.presentation.base.model.Event
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.*

class TaskEditViewModel(
    private val taskUseCase: TaskEditUseCase,
    getAllUsersUseCase: GetAllUsersUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    val allUsersLiveData = getAllUsersUseCase.executeUseCase()

    private val _prettyDate = MutableLiveData<Event<String>>()
    val prettyDate: LiveData<Event<String>>
        get() = _prettyDate

    private val _taskDone = MutableLiveData<Event<Response<Task>>>()
    val taskDone: LiveData<Event<Response<Task>>>
        get() = _taskDone


    fun currentUserId() = firebaseAuth.currentUser?.uid.orEmpty()

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
