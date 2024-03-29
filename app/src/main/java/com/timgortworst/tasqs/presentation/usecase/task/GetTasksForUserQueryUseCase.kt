package com.timgortworst.tasqs.presentation.usecase.task

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.domain.usecase.SuspendUseCase

interface GetTasksForUserQueryUseCase : SuspendUseCase<FirestoreRecyclerOptions.Builder<Task>, None>

