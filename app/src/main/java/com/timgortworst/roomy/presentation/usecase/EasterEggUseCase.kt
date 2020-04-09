package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.easteregg.EasterEggUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.EasterEgg

interface EasterEggUseCase : UseCase<EasterEgg?, EasterEggUseCaseImpl.Params>