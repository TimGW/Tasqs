package com.timgortworst.tasqs.domain.usecase.task

import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.presentation.usecase.task.GetAllTasksUseCase

class GetAllTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetAllTasksUseCase {

    override suspend fun execute(params: None) = taskRepository.getAllTasksQuery()
}

