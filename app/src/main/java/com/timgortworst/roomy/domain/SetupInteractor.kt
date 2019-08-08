package com.timgortworst.roomy.domain

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.EventRepository
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SetupInteractor
@Inject
constructor(private val householdRepository: HouseholdRepository,
            private val userRepository: UserRepository,
            private val eventRepository: EventRepository) {

    suspend fun initializeHousehold(): String? {
        return householdRepository.createHousehold()
    }

    suspend fun createUser() {
        userRepository.createUser()
    }

    suspend fun switchHousehold(householdId: String, role: String) {
        val userId = userRepository.updateUser(householdId = householdId, role = role)

        // remove events assigned to user
        eventRepository.getEventsForUser(userId).forEach {
            eventRepository.eventCollectionRef.document(it.eventId).delete()
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
        val housholdRef = householdRepository.householdsCollectionRef.document(householdId)
        val household = housholdRef.get().await().toObject(Household::class.java) as Household
        return household.userIdBlackList.contains(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
    }

    suspend fun isHouseholdFull(referredHouseholdId: String): Boolean {
        val userList = userRepository.getUserListForHousehold(referredHouseholdId) ?: return true
        return userList.size >= 10
    }

    suspend fun isIdSimilarToActiveId(referredHouseholdId: String): Boolean {
        return referredHouseholdId == getHouseholdIdForUser()
    }
}
