package com.timgortworst.roomy.presentation.usecase.splash

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.splash.ForcedUpdateUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.UpdateAction

interface ForcedUpdateUseCase : UseCase<UpdateAction, ForcedUpdateUseCaseImpl.Params>