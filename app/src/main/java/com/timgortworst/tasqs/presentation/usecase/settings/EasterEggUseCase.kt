package com.timgortworst.tasqs.presentation.usecase.settings

import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.settings.EasterEggUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.EasterEgg

interface EasterEggUseCase : UseCase<EasterEgg?, EasterEggUseCaseImpl.Params>