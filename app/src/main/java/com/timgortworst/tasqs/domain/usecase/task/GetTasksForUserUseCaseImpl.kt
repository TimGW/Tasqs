package com.timgortworst.tasqs.domain.usecase.task

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.presentation.usecase.task.GetTasksForUserUseCase

class GetTasksForUserUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTasksForUserUseCase {

    override suspend fun execute(params: Unit?) = taskRepository.getTasksForUserQuery(
        FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    )
}

