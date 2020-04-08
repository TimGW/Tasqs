package com.timgortworst.roomy.presentation.features.task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.user.GetAllUsersUseCase
import com.timgortworst.roomy.domain.usecase.task.CreateOrUpdateTaskUseCase
import com.timgortworst.roomy.presentation.base.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.*

class TaskEditViewModel(
    private val createOrUpdateTaskUseCase: UseCase<Flow<Response<Task>>, CreateOrUpdateTaskUseCase.Params>,
    private val getCurrentFbUserUseCase: UseCase<FirebaseUser?, Unit>,
    getAllUsersUseCase: UseCase<LiveData<Response<List<User>>>, Unit>
) : ViewModel() {
    val allUsersLiveData = getAllUsersUseCase.execute()

    private val _prettyDate = MutableLiveData<Event<String>>()
    val prettyDate: LiveData<Event<String>>
        get() = _prettyDate

    private val _taskDone = MutableLiveData<Event<Response<Task>>>()
    val taskDone: LiveData<Event<Response<Task>>>
        get() = _taskDone

    fun currentUserId() = getCurrentFbUserUseCase.execute()?.uid.orEmpty()

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
        val params = CreateOrUpdateTaskUseCase.Params(task)
        createOrUpdateTaskUseCase.execute(params).collect {
            _taskDone.value = Event(it)
        }
    }
}
