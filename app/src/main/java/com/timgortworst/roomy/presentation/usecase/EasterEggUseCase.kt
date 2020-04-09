package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.easteregg.EasterEggUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.EasterEgg
import kotlinx.coroutines.flow.Flow

interface EasterEggUseCase : UseCase<EasterEgg?, EasterEggUseCaseImpl.Params>