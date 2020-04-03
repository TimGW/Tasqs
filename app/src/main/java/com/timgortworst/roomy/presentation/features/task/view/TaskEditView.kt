package com.timgortworst.roomy.presentation.features.task.view

import androidx.annotation.StringRes
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.task.TaskUser

interface TaskEditView {
    fun presentUserList(filteredUserList: List<TaskUser>)
    fun setPluralSpinner()
    fun setSingularSpinner()
    fun presentCurrentUser(currentUser: TaskUser?)
    fun presentError(@StringRes stringRes: Int = R.string.error_generic)
}