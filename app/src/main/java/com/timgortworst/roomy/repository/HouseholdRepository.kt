package com.timgortworst.roomy.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.utils.Constants
import com.timgortworst.roomy.utils.Constants.CATEGORIES_COLLECTION_REF
import com.timgortworst.roomy.utils.GenerateData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseholdRepository @Inject constructor() {
    val householdsCollectionRef = FirebaseFirestore.getInstance().collection(Constants.HOUSEHOLD_COLLECTION_REF)

    suspend fun createHousehold(): String? {
        val householdID = householdsCollectionRef.document().id

        val categories = GenerateData.eventCategories()
        for (category in categories) {
            val householdSubEventCategories = householdsCollectionRef.document().collection(CATEGORIES_COLLECTION_REF).document()
            category.categoryId = householdSubEventCategories.id

            try {
                // todo batched writes
                householdSubEventCategories.set(category).await() // todo coroutine async and join for multithreading
            } catch (e: FirebaseFirestoreException) {
                return null
            }
        }

        return try {
            householdsCollectionRef.document().set(Household(householdId = householdID)).await()
            householdID
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
        // todo delete sub items
//        val batch = db.batch()
//        db.collection(CATEGORIES_COLLECTION_REF).get().result?.forEach { batch.delete(it.reference).commit().await() }
//        db.collection(EVENT_COLLECTION_REF).get().result?.forEach { batch.delete(it.reference).commit().await() }

//        // delete household
//        householdsCollectionRef
//            .document(householdId)
//            .delete().await()
    }

//
//    suspend fun isUserBanned(householdId: String): Boolean {
//        val housholdRef = householdsCollectionRef.document(householdId)
//        val household = housholdRef.get().await().toObject(Household::class.java) as Household
//        return household.blackList.contains(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
//    }

    companion object {
        private const val TAG = "HouseholdRepository"
    }
}
