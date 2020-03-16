package com.timgortworst.roomy.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.domain.model.Household
import com.timgortworst.roomy.domain.model.Household.Companion.HOUSEHOLD_COLLECTION_REF
import kotlinx.coroutines.tasks.await

class HouseholdRepository(
    db: FirebaseFirestore
) {
    private val householdsCollectionRef = db.collection(HOUSEHOLD_COLLECTION_REF)

    suspend fun createHousehold(): String {
        val household = householdsCollectionRef.document()
        household.set(Household(householdId = household.id)).await()
        return household.id
    }

    suspend fun deleteHousehold(householdId: String) {
        householdsCollectionRef.document(householdId).delete().await()
    }
}
