package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.User

class UserUseCase(private val householdRepository: HouseholdRepository,
                  private val userRepository: UserRepository,
                  private val eventRepository: EventRepository) {
    private val uId = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun getCurrentUser() = userRepository.getUser(uId)

    suspend fun deleteAndBanUser(user: User) {
        removeEventsAssignedToUser(user.userId)

        addUserToBlackList(user.userId)

        // clear household id from user document
        userRepository.updateUser(userId = user.userId, householdId = "")
    }

    suspend fun updateUser(name: String, email: String) {
        userRepository.updateUser(name = name, email = email)
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

    suspend fun getUsersForHousehold(): Query {
        return userRepository.getUsersForHousehold(userRepository.getHouseholdIdForUser(uId))
    }
}
