package com.timgortworst.tasqs.presentation.usecase.task

import com.google.firebase.firestore.Query
import com.timgortworst.tasqs.domain.usecase.SuspendUseCase

interface GetTasksForUserUseCase : SuspendUseCase<Query, Unit>

