package com.timgortworst.roomy.presentation.features.task.presenter

import android.text.Editable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.textfield.TextInputEditText
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Response
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskUser
import com.timgortworst.roomy.domain.usecase.TaskUseCase
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.task.view.TaskEditView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.*

// todo refactor this class to a viewmodel
class TaskEditPresenter(
    private val view: TaskEditView,
    private val taskUseCase: TaskUseCase,
    private val userUseCase: UserUseCase
) : DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun getUsers() = scope.launch {
        userUseCase.getAllUsersForHousehold().collect { response ->

            when (response) {
                Response.Loading -> { } // todo
                is Response.Success -> {
                    val currentUser = userUseCase.getCurrentUser() ?: return@collect
                    if (response.data!!.filterNot { it.userId == currentUser.userId }.isEmpty()) {
                        view.presentCurrentUser(TaskUser(currentUser.userId, currentUser.name))
                    } else {
                        view.presentUserList(response.data.map { TaskUser(it.userId, it.name) })
                    }
                }
                is Response.Error -> { } // todo
            }
        }
    }

    fun formatDate(zonedDateTime: ZonedDateTime) {
        val formattedDayOfMonth = zonedDateTime.dayOfMonth.toString()
        val formattedMonth = zonedDateTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val formattedYear = zonedDateTime.year.toString()
        view.presentFormattedDate(formattedDayOfMonth, formattedMonth, formattedYear)
    }

    fun editTaskDone(task: Task) = scope.launch {
        if (task.description.isEmpty()) {
            view.presentEmptyDescriptionError(R.string.task_edit_error_empty_description)
            return@launch
        }

        taskUseCase.createOrUpdateTask(task).collect()

        view.finishActivity()
    }

    fun checkForPluralRecurrenceSpinner(numberInput: String) {
        when {
            numberInput.toIntOrNull()?.equals(1) == true || numberInput.isBlank() ->
                view.setSingularSpinner()
            else -> view.setPluralSpinner()
        }
    }

    fun disableInputZero(editable: Editable?) {
        editable?.let {
            val input = it.toString()
            if (input.isNotEmpty() && input.first() == '0') {
                it.replace(0, 1, "1")
            }
        }
    }

    fun disableEmptyInput(view: TextInputEditText?, hasFocus: Boolean) {
        view?.apply {
            if (text.toString().isBlank() && !hasFocus) {
                setText("1")
            }
        }
    }
}
