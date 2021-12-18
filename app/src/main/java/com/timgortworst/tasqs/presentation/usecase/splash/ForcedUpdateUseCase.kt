package com.timgortworst.tasqs.presentation.usecase.splash

import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.splash.ForcedUpdateUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.UpdateAction

interface ForcedUpdateUseCase : FlowUseCase<UpdateAction, ForcedUpdateUseCaseImpl.Params>