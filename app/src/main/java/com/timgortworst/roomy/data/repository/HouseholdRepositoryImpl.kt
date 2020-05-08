package com.timgortworst.roomy.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.domain.model.Household
import com.timgortworst.roomy.domain.model.Household.Companion.HOUSEHOLD_COLLECTION_REF
import com.timgortworst.roomy.domain.repository.HouseholdRepository
import kotlinx.coroutines.tasks.await

class HouseholdRepositoryImpl : HouseholdRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val householdCollection = db.collection(HOUSEHOLD_COLLECTION_REF)

    override suspend fun createHousehold(): String {
        val household = householdCollection.document()
        household.set(
            Household(
                householdId = household.id
            )
        ).await()
        return household.id
    }

    override suspend fun deleteHousehold(householdId: String) {
        householdCollection.document(householdId).delete().await()
    }
}
