package com.timgortworst.roomy.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.utils.Constants
import com.timgortworst.roomy.utils.Constants.AGENDA_EVENT_CATEGORIES_COLLECTION_REF
import com.timgortworst.roomy.utils.GenerateData
import kotlinx.coroutines.tasks.await

class HouseholdRepository(db: FirebaseFirestore, private val sharedPref: HuishoudGenootSharedPref) {

    private val householdsCollectionRef = db.collection(Constants.HOUSEHOLD_COLLECTION_REF)
    private val householdDocRef = householdsCollectionRef.document()

    suspend fun createNewHousehold(): String? {
        val householdID = householdDocRef.id

        // update local household id
        sharedPref.setActiveHouseholdId(householdID)

        val categories = GenerateData.eventCategories()
        for (category in categories) {
            val householdSubEventCategories =
                householdDocRef.collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF).document()
            category.categoryId = householdSubEventCategories.id

            householdSubEventCategories.set(category)
        }

        return try {
            householdDocRef.set(Household(householdId = householdID)).await()
            householdID
        } catch (e: FirebaseFirestoreException) {
            null
        }
    }

    suspend fun removeHousehold(householdId: String) {
        //delete sub items
        householdDocRef.collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF).document().delete().await()
        // delete household
        householdsCollectionRef
            .document(householdId)
            .delete().await()
    }

    companion object {
        private const val TAG = "TIMTIM"
    }
}
