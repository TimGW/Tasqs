package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import org.koin.core.KoinComponent

class SetupUseCase(private val householdRepository: HouseholdRepository,
                   private val userRepository: UserRepository,
                   private val eventRepository: EventRepository) : KoinComponent {

    suspend fun initializeHousehold(): String? {
        return householdRepository.createHousehold()
    }

    suspend fun createUser() {
        userRepository.createUser()
    }

    suspend fun switchHousehold(householdId: String, role: String) {
        val currentUserId = userRepository.getCurrentUserId() ?: return

        userRepository.updateUser(householdId = householdId, role = role)

        // remove events assigned to user
        eventRepository.getEventsForUser(currentUserId).forEach {
            eventRepository.deleteEvent(it.eventId)
        }
    }

    suspend fun getHouseholdIdForUser() = userRepository.getHouseholdIdForUser()

    suspend fun getUserListForHousehold(householdId: String): List<User>? {
        return userRepository.getUserListForHousehold(householdId)
    }

    suspend fun deleteHousehold(householdId: String) {
        householdRepository.deleteHousehold(householdId)
    }

    suspend fun userBlackListedForHousehold(householdId: String): Boolean {
        val household = householdRepository.getHousehold(householdId)
        return household?.userIdBlackList?.contains(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                ?: false
    }

    suspend fun isHouseholdFull(referredHouseholdId: String): Boolean {
        val userList = userRepository.getUserListForHousehold(referredHouseholdId) ?: return true
        return userList.size >= 10
    }

    suspend fun isIdSimilarToActiveId(referredHouseholdId: String): Boolean {
        return referredHouseholdId == getHouseholdIdForUser()
    }
}
