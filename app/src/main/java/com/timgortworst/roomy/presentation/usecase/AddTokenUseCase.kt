package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.usecase.SuspendUseCase
import com.timgortworst.roomy.domain.usecase.user.AddTokenUseCaseImpl

interface AddTokenUseCase : SuspendUseCase<Unit, AddTokenUseCaseImpl.Params>