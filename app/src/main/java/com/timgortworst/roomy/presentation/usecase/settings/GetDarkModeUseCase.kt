package com.timgortworst.roomy.presentation.usecase.settings

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.task.DeleteTaskUseCaseImpl

interface GetDarkModeUseCase : UseCase<Int, Unit>