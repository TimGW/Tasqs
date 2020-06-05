package com.timgortworst.roomy.presentation.usecase.task

import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.task.CompleteTaskUseCaseImpl
import com.timgortworst.roomy.domain.usecase.task.GetTaskUseCaseImpl

interface GetTaskUseCase : UseCase<Task, GetTaskUseCaseImpl.Params>