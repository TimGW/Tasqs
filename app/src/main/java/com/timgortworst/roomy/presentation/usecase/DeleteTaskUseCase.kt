package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.task.DeleteTaskUseCaseImpl
import kotlinx.coroutines.flow.Flow

interface DeleteTaskUseCase : UseCase<Flow<Response<Nothing>>, DeleteTaskUseCaseImpl.Params>