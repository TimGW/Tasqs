package com.timgortworst.roomy.domain.usecase.task

import com.google.firebase.firestore.Query
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.domain.usecase.SuspendUseCase

class GetAllTasksUseCase(
    private val taskRepository: TaskRepository
) : SuspendUseCase<Query> {

    override suspend fun invoke() = taskRepository.getAllTasksQuery()
}

