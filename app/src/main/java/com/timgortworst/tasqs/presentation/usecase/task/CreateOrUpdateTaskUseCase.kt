package com.timgortworst.tasqs.presentation.usecase.task

import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.task.CreateOrUpdateTaskUseCaseImpl

interface CreateOrUpdateTaskUseCase :
    UseCase<Task, CreateOrUpdateTaskUseCaseImpl.Params>