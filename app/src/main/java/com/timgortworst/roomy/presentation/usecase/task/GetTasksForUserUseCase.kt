package com.timgortworst.roomy.presentation.usecase.task

import com.google.firebase.firestore.Query
import com.timgortworst.roomy.domain.usecase.SuspendUseCase

interface GetTasksForUserUseCase : SuspendUseCase<Query, Unit>

