package com.timgortworst.roomy.domain.usecase.splash

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.HouseholdRepository
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.presentation.RoomyApp.Companion.LOADING_DELAY
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import com.timgortworst.roomy.presentation.usecase.splash.SwitchHouseholdUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SwitchHouseholdUseCaseImpl(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler
) : SwitchHouseholdUseCase {
    private val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

    data class Params(val newId: String)

    override fun execute(params: Params?) = channelFlow {
        checkNotNull(params)

        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(LOADING_DELAY)
            offer(Response.Loading)
        }

        try {
            val oldId = userRepository.getUser()?.householdId ?: return@channelFlow
            val currentUserId = fbAuth.currentUser?.uid ?: return@channelFlow

            // remove old tasks assigned to user
            taskRepository.deleteTasks(
                taskRepository.getTasksForUser(currentUserId)
            )

            // delete old household if no other user is left to assigned to it
            userRepository.getAllUsersForHousehold(oldId).let {
                if (it.size <= 1) householdRepository.deleteHousehold(oldId)
            }

            // update current user with new household ID and role
            userRepository.updateUser(
                householdId = params.newId,
                isAdmin = false
            )
            offer(Response.Success(StartUpAction.TriggerMainFlow))
        } catch (e: FirebaseFirestoreException) {
            offer(Response.Error(errorHandler.getError(e)))
        } finally {
            loadingJob.cancel()
        }
    }.flowOn(Dispatchers.IO)
}
