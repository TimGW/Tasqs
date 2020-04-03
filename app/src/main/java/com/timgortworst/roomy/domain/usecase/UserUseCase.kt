package com.timgortworst.roomy.domain.usecase

import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.firestore.User
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class UserUseCase(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
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

    suspend fun getHouseholdIdForUser() = householdRepository.getHouseholdId()

    fun removeUserFromHousehold(user: User) = channelFlow {
        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(500) // delay 0.5s before showing loading
            offer(Response.Loading)
        }

        try {
            removeEventsAssignedToUser(user.userId)
            val householdId = householdRepository.createHousehold()
            userRepository.updateUser(userId = user.userId, householdId = householdId, isAdmin = true)

            offer(Response.Success(user))
        } catch (e: FirebaseFirestoreException) {
            offer(Response.Error(errorHandler.getError(e)))
        } finally {
            loadingJob.cancel()
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun removeEventsAssignedToUser(userId: String) {
        val tasks = taskRepository.getTasksForUser(userId)
        taskRepository.deleteTasks(tasks)
    }

    suspend fun addTokenToUser(token: String) {
        userRepository.addUserToken(token = token)
    }
}
