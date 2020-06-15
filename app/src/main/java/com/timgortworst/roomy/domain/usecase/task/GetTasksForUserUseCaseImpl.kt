package com.timgortworst.roomy.domain.usecase.task

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.presentation.usecase.task.GetTasksForUserUseCase

class GetTasksForUserUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTasksForUserUseCase {

    override suspend fun execute(params: Unit?) = taskRepository.getTasksForUserQuery(
        FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    )
}

