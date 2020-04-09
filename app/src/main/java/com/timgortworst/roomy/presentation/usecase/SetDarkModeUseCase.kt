package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.settings.SetDarkModeUseCaseImpl
import com.timgortworst.roomy.domain.usecase.task.DeleteTaskUseCaseImpl

interface SetDarkModeUseCase : UseCase<Unit, SetDarkModeUseCaseImpl.Params>