package com.timgortworst.roomy.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.Household
import com.timgortworst.roomy.domain.model.Household.Companion.HOUSEHOLD_COLLECTION_REF
import com.timgortworst.roomy.presentation.RoomyApp.Companion.TAG
import kotlinx.coroutines.tasks.await

class HouseholdRepository {
    private val householdsCollectionRef = FirebaseFirestore.getInstance().collection(HOUSEHOLD_COLLECTION_REF)

    suspend fun createHousehold(): String? {
        val household = householdsCollectionRef.document()
        return try {
            household.set(Household(householdId = household.id)).await()
            household.id
        } catch (e: FirebaseFirestoreException) {
            null
        }
    }

    suspend fun deleteHousehold(householdId: String) {
        try {
            householdsCollectionRef.document(householdId).delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }
}
