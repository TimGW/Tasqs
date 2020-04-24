package com.timgortworst.roomy.presentation.usecase.settings

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.settings.EasterEggUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.EasterEgg

interface EasterEggUseCase : UseCase<EasterEgg?, EasterEggUseCaseImpl.Params>