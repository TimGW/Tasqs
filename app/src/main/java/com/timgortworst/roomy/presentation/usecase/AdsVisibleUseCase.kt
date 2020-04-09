package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.usecase.UseCase
import kotlinx.coroutines.flow.Flow

interface AdsVisibleUseCase : UseCase<Flow<Boolean>, Unit>