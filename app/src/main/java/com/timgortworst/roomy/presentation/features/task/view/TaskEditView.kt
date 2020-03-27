package com.timgortworst.roomy.presentation.features.task.view

import androidx.annotation.StringRes
import com.timgortworst.roomy.domain.model.TaskUser
import com.timgortworst.roomy.domain.model.User

interface TaskEditView {
    fun presentUserList(filteredUserList: List<TaskUser>)
    fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String)
    fun presentEmptyDescriptionError(@StringRes errorMessage: Int)
    fun finishActivity()
    fun setPluralSpinner()
    fun setSingularSpinner()
    fun presentCurrentUser(currentUser: TaskUser?)
}