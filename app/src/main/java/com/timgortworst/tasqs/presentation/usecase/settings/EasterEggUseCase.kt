package com.timgortworst.tasqs.presentation.usecase.settings

import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.settings.EasterEggUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.EasterEgg

interface EasterEggUseCase : FlowUseCase<EasterEgg?, EasterEggUseCaseImpl.Params>