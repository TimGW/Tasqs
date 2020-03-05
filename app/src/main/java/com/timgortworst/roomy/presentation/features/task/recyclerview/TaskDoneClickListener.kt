package com.timgortworst.roomy.presentation.features.task.recyclerview

import com.timgortworst.roomy.domain.model.Task

interface TaskDoneClickListener {
    fun onTaskDoneClicked(task: Task, position: Int)
}