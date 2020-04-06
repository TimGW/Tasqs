package com.timgortworst.roomy.domain.application.household

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.repository.HouseholdRepository
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.domain.application.UseCase
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SwitchHouseholdUseCase(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler
) : UseCase<Flow<Response<StartUpAction>>> {
    private lateinit var newId: String

    fun init(newId: String): SwitchHouseholdUseCase {
        this.newId = newId
        return this
    }

    override fun invoke()= flow {
        emit(Response.Loading)
        try {
            val oldId = userRepository.getUser()?.householdId ?: return@flow
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@flow

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
                householdId = newId,
                isAdmin = false
            )
            emit(Response.Success(StartUpAction.TriggerMainFlow))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}
