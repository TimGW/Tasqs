package com.timgortworst.roomy.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseholdRepository @Inject constructor() {
    val householdsCollectionRef = FirebaseFirestore.getInstance().collection(Constants.HOUSEHOLD_COLLECTION_REF)

    suspend fun createHousehold(): String? {
        val household = householdsCollectionRef.document()
        return try {
            household.set(Household(householdId = household.id)).await()
            household.id
        } catch (e: FirebaseFirestoreException) {
            null
        }
    }

    suspend fun updateHousehold(
        householdId: String,
        blackList: MutableList<String> = mutableListOf()
    ) {
        val householdDocRef = householdsCollectionRef.document(householdId)

        val fieldMap = mutableMapOf<String, Any>()
        if (householdId.isNotBlank()) fieldMap[Constants.HOUSEHOLD_ID_REF] = householdId
        if (blackList.isNotEmpty()) fieldMap[Constants.HOUSEHOLD_BLACKLIST_REF] = blackList

        householdDocRef.set(fieldMap, SetOptions.merge()).await()
    }

    suspend fun deleteHousehold(householdId: String) {
        householdsCollectionRef.document(householdId).delete().await()
    }

    companion object {
        private const val TAG = "HouseholdRepository"
    }
}
