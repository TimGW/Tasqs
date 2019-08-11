package com.timgortworst.roomy.domain.usecase

import com.timgortworst.roomy.data.model.Household
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.utils.Constants
import com.timgortworst.roomy.ui.features.user.presenter.UserListPresenter
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserUseCase
@Inject
constructor(private val householdRepository: HouseholdRepository,
            private val userRepository: UserRepository,
            private val eventRepository: EventRepository) {

    suspend fun getCurrentUser() = userRepository.getUser()

    suspend fun deleteAndBanUser(user: User) {
        removeEventsAssignedToUser(user.userId)

        addUserToBlackList(user.userId)

        // remove id from user document
        userRepository.userCollectionRef
                .document(user.userId)
                .update(Constants.USER_HOUSEHOLDID_REF, "")
    }

    private suspend fun addUserToBlackList(userId: String) {
        val household = householdRepository.householdsCollectionRef
                .document(userRepository.getHouseholdIdForUser(userId))
                .get()
                .await()
                .toObject(Household::class.java) as Household

        household.userIdBlackList.add(userId)
        householdRepository.updateHousehold(household.householdId, household.userIdBlackList)
    }

    private suspend fun removeEventsAssignedToUser(userId: String) {
        eventRepository.getEventsForUser(userId).forEach {
            eventRepository.eventCollectionRef.document(it.eventId).delete()
        }
    }

    fun detachUserListener() {
        userRepository.detachUserListener()
    }

    suspend fun listenToUsers(userListPresenter: UserListPresenter) {
        userRepository.listenToUsersForHousehold(userRepository.getHouseholdIdForUser(), userListPresenter)
    }
}
