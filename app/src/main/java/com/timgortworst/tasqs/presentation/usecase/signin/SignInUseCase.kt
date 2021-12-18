package com.timgortworst.tasqs.presentation.usecase.signin

import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.signin.SignInUseCaseImpl

interface SignInUseCase : FlowUseCase<String, SignInUseCaseImpl.Params>