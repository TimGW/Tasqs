package com.timgortworst.roomy.domain

import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.EventRepository
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.user.presenter.UserListPresenter
import com.timgortworst.roomy.utils.Constants.USER_HOUSEHOLDID_REF
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserListInteractor
@Inject
constructor(private val householdRepository: HouseholdRepository,
            private val userRepository: UserRepository,
            private val eventRepository: EventRepository) {

    suspend fun getCurrentUser() = userRepository.getUser()

    suspend fun deleteAndBanUser(user: User) {
        removeEventsAssignedToUser(user.userId)

        // remove id from user document
        userRepository.userCollectionRef
                .document(user.userId)
                .update(USER_HOUSEHOLDID_REF, "")
                .await()

        addUserToBlackList(user.userId)
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

    // todo use transactions
    private suspend fun removeEventsAssignedToUser(userId: String) {
        eventRepository.getEventsForUser(userId).forEach {
            eventRepository.eventCollectionRef.document(it.eventId).delete()
        }
    }

    fun detachUserListener() {
        userRepository.detachUserListener()
    }

    suspend fun listenToUsers(userListPresenter: UserListPresenter, isAirplaneModeEnabled: Boolean) {
        userRepository.listenToUsersForHousehold(userRepository.getHouseholdIdForUser(), userListPresenter, isAirplaneModeEnabled)
    }
}
