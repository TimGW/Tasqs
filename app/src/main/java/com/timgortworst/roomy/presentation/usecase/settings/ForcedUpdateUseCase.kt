package com.timgortworst.roomy.presentation.usecase.settings

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.forcedupdate.ForcedUpdateUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.UpdateAction

interface ForcedUpdateUseCase : UseCase<UpdateAction, ForcedUpdateUseCaseImpl.Params>