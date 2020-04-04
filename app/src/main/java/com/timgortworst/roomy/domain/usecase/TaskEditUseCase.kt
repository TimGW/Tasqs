package com.timgortworst.roomy.domain.usecase

import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.task.Task
import com.timgortworst.roomy.domain.model.task.TaskMetaData
import com.timgortworst.roomy.domain.model.task.TaskRecurrence
import com.timgortworst.roomy.domain.utils.TimeOperations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import kotlin.coroutines.coroutineContext

class TaskEditUseCase(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val householdRepository: HouseholdRepository,
    private val errorHandler: ErrorHandler
) {

    suspend fun getCurrentUser() = userRepository.getUser(FirebaseAuth.getInstance().currentUser?.uid)

    fun getAllUsersForHousehold() = liveData(Dispatchers.IO) {
        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(500) // delay 0.5s before showing loading
            emit(Response.Loading)
        }
        try {
            emit(Response.Success(userRepository.getAllUsersForHousehold(householdRepository.getHouseholdId())))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        } finally {
            loadingJob.cancel()
        }
    }

    fun createOrUpdateTask(task: Task) = callbackFlow {
        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(500) // delay 0.5s before showing loading
            offer(Response.Loading)
        }

        try {
            // temporary disable the done button
            val result = task.apply { isDoneEnabled = false }

            if (task.id.isEmpty()) {
                taskRepository.createTask(result)
            } else {
                taskRepository.updateTask(result)
            }
            offer(Response.Success(task))
        } catch (e: FirebaseFirestoreException) {
            offer(Response.Error(errorHandler.getError(e)))
        } finally {
            awaitClose { loadingJob.cancel() }
        }
    }.flowOn(Dispatchers.IO)
}

