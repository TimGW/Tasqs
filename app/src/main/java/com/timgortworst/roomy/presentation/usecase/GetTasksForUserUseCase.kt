package com.timgortworst.roomy.presentation.usecase

import com.google.firebase.firestore.Query
import com.timgortworst.roomy.domain.usecase.SuspendUseCase

interface GetTasksForUserUseCase : SuspendUseCase<Query, Unit>
