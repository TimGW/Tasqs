package com.timgortworst.roomy.presentation.usecase

import androidx.lifecycle.LiveData
import com.timgortworst.roomy.domain.usecase.UseCase

interface AdsVisibleUseCase : UseCase<LiveData<Boolean>, Unit>