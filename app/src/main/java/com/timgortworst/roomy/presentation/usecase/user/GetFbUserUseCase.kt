package com.timgortworst.roomy.presentation.usecase.user

import com.google.firebase.auth.FirebaseUser
import com.timgortworst.roomy.domain.usecase.UseCase

interface GetFbUserUseCase : UseCase<FirebaseUser?, Unit>