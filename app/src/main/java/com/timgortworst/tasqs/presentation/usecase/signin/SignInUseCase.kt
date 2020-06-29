package com.timgortworst.tasqs.presentation.usecase.signin

import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.signin.SignInUseCaseImpl

interface SignInUseCase : UseCase<String, SignInUseCaseImpl.Params>