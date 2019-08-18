package com.timgortworst.roomy.domain.usecase

import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.presentation.features.user.presenter.UserListPresenter
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

        // clear household id from user document
        userRepository.updateUser(userId = user.userId, householdId = "")
    }

    private suspend fun addUserToBlackList(userId: String) {
        val household = householdRepository.getHousehold(userRepository.getHouseholdIdForUser(userId))
        household?.userIdBlackList?.add(userId)
        householdRepository.updateHousehold(household?.householdId, household?.userIdBlackList)
    }

    private suspend fun removeEventsAssignedToUser(userId: String) {
        eventRepository.getEventsForUser(userId).forEach {
            eventRepository.deleteEvent(it.eventId)
        }
    }

    fun detachUserListener() {
        userRepository.detachUserListener()
    }

    suspend fun listenToUsers(userListPresenter: UserListPresenter) {
        userRepository.listenToUsersForHousehold(userRepository.getHouseholdIdForUser(), userListPresenter)
    }
}
