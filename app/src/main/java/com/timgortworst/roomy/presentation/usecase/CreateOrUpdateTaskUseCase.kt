package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.task.CreateOrUpdateTaskUseCaseImpl
import kotlinx.coroutines.flow.Flow

interface CreateOrUpdateTaskUseCase :
    UseCase<Task, CreateOrUpdateTaskUseCaseImpl.Params>