package com.timgortworst.roomy.domain.usecase.account

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.presentation.RoomyApp
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import com.timgortworst.roomy.presentation.usecase.signin.ValidationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ValidationUseCaseImpl(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : ValidationUseCase {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    data class Params(val referredId: String)

    override fun execute(params: Params?) = channelFlow {
        checkNotNull(params)

        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(RoomyApp.LOADING_DELAY)
            offer(Response.Loading)
        }

        try {
            val currentId = fetchHouseholdId() // fetch here to update local cache
            offer(validate(currentId, params.referredId))
        } catch (e: FirebaseFirestoreException) {
            offer(Response.Error(errorHandler.getError(e)))
        } finally {
            loadingJob.cancel()
        }
    }.flowOn(Dispatchers.IO)

    private fun validate(currentId: String, referredId: String): Response<StartUpAction> {
       return when {
            // first check if user has valid authentication
            (auth.currentUser == null ||
                    auth.currentUser?.uid?.isBlank() == true) -> {
                Response.Success(StartUpAction.TriggerSignInFlow)
            }

            // check if the user accepted an invite link
            referredId.isNotBlank() -> Response.Success(referredUser(currentId, referredId))

            // continue to the main activity
            else -> Response.Success(StartUpAction.TriggerMainFlow)
        }
    }

    private fun referredUser(currentId: String, referredId: String): StartUpAction {
        return when (referredId) {
            currentId -> StartUpAction.DialogSameId // user is already in the same household
            else -> StartUpAction.DialogOverrideId(referredId) // user will override
        }
    }

    private suspend fun fetchHouseholdId() = userRepository.getUser()?.householdId.orEmpty()
}
