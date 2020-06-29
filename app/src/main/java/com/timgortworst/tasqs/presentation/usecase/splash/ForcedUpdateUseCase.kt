package com.timgortworst.tasqs.presentation.usecase.splash

import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.splash.ForcedUpdateUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.UpdateAction

interface ForcedUpdateUseCase : UseCase<UpdateAction, ForcedUpdateUseCaseImpl.Params>