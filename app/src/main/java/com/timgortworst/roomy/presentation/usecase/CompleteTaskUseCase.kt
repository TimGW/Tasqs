package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.task.CompleteTaskUseCaseImpl
import kotlinx.coroutines.flow.Flow

interface CompleteTaskUseCase : UseCase<Flow<Response<Nothing>>, CompleteTaskUseCaseImpl.Params>