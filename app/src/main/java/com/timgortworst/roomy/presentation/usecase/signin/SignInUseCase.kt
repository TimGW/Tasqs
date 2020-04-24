package com.timgortworst.roomy.presentation.usecase.signin

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.account.SignInUseCaseImpl

interface SignInUseCase : UseCase<String, SignInUseCaseImpl.Params>