package com.timgortworst.tasqs.presentation.base.model

sealed class TaskInfoAction {
    object Loading : TaskInfoAction()
    object Finish : TaskInfoAction()
}
