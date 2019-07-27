package com.timgortworst.roomy.domain

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.CategoryRepository
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.utils.GenerateData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SetupInteractor
@Inject
constructor(private val categoryRepository: CategoryRepository,
            private val householdRepository: HouseholdRepository,
            private val userRepository: UserRepository) {

    suspend fun initializeHousehold(): String? {
        val householdId = householdRepository.createHousehold()

        if (householdId?.isNotEmpty() == true) {
            GenerateData.setupCategoriesForHousehold(householdId).forEach {
                categoryRepository.createCategory(it.name, it.description, it.householdId)
            }
        }
        return householdId
    }

    suspend fun updateUser(householdId: String, role: String) {
        userRepository.updateUser(householdId = householdId, role = role)
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
}
