package com.timgortworst.roomy.domain

import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.EventRepository
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.utils.Constants.USER_HOUSEHOLDID_REF
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserListInteractor
@Inject
constructor(private val householdRepository: HouseholdRepository,
            private val userRepository: UserRepository,
            private val eventRepository: EventRepository) {

    suspend fun getCurrentUser() = userRepository.readCurrentUser()

    fun getCurrentUserId() = userRepository.getCurrentUserId()

    suspend fun getHouseholdIdForCurrentUser() = userRepository.readHouseholdIdForCurrentUser()

    suspend fun getUsersForHouseholdId(householdId: String) = userRepository.readUsersForHouseholdId(householdId)

    suspend fun deleteAndBanUser(user: User) {
        // set user on blacklist
        addUserToBlackList(user.userId)

        // remove id from user document
        userRepository.userCollectionRef
                .document(user.userId)
                .update(USER_HOUSEHOLDID_REF, "")
                .await()

        // remove all events for that user
        removeEventsForUser(user.userId)
    }

    private suspend fun addUserToBlackList(userId: String) {
        val household = householdRepository.householdsCollectionRef
                .document(userRepository.readHouseholdIdForUser(userId))
                .get()
                .await()
                .toObject(Household::class.java) as Household

        household.blackList.add(userId)
        householdRepository.updateHousehold(household.householdId, household.blackList)
    }

    private suspend fun removeEventsForUser(userId: String) {
//        val householdRef = householdRepository.householdsCollectionRef
//                .document(userRepository.readHouseholdIdForUser(userId))
//
//        val event = householdRef.collection(EVENT_COLLECTION_REF).document().whereEqualTo("user.userId", userId).get() as Event
//
//        householdRef
//            .collection(EVENT_COLLECTION_REF)
//            .document(event.)
//            .delete()
//            .await()
    }

}