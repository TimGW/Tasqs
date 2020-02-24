package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.User

class SetupUseCase(private val householdRepository: HouseholdRepository,
                   private val userRepository: UserRepository,
                   private val eventRepository: EventRepository) {
    private val uId = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun initializeHousehold(fireBaseUser: FirebaseUser): String? {
        val householdId = householdRepository.createHousehold() ?: return null
        userRepository.createUser(householdId, fireBaseUser)
        return householdId
    }

    suspend fun switchHousehold(householdId: String, role: String) {
        val currentUserId = uId ?: return

        userRepository.updateUser(householdId = householdId, role = role)

        // remove events assigned to user
        eventRepository.getEventsForUser(currentUserId).forEach {
            eventRepository.deleteEvent(it.eventId)
        }
    }

    suspend fun currentHouseholdIdForCurrentUser() = userRepository.getHouseholdIdForUser(uId)

    suspend fun userListForCurrentHousehold(): List<User>? {
        return userRepository.getUserListForHousehold(userRepository.getHouseholdIdForUser(uId))
    }

    suspend fun deleteHousehold(householdId: String) {
        householdRepository.deleteHousehold(householdId)
    }

//    suspend fun userBlackListedForHousehold(householdId: String): Boolean {
//        val household = householdRepository.getHousehold(householdId)
//        return household?.userIdBlackList?.contains(uId.orEmpty()) ?: false
//    }

//    suspend fun isHouseholdFull(referredHouseholdId: String): Boolean {
//        val userList = userRepository.getUserListForHousehold(referredHouseholdId) ?: return true
//        return userList.size >= 10
//    }

    suspend fun isIdSimilarToActiveId(referredHouseholdId: String): Boolean {
        return referredHouseholdId == userRepository.getHouseholdIdForUser(uId)
    }
}
