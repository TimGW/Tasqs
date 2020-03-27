package com.timgortworst.roomy.presentation.features.task.adapter

import com.timgortworst.roomy.domain.model.Task

interface TaskClickListener {
    fun onTaskDoneClicked(task: Task, position: Int)
    fun onTaskInfoClicked(task: Task)
}