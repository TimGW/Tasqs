package com.timgortworst.tasqs.presentation.usecase.task

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.usecase.SuspendUseCase

interface GetTasksForUserUseCase : SuspendUseCase<FirestoreRecyclerOptions.Builder<Task> , Unit>

