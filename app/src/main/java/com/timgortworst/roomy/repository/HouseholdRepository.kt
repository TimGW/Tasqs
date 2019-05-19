package com.timgortworst.roomy.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.utils.Constants
import com.timgortworst.roomy.utils.Constants.AGENDA_EVENT_CATEGORIES_COLLECTION_REF
import com.timgortworst.roomy.utils.GenerateData

class HouseholdRepository(db: FirebaseFirestore) {

    private val householdsCollectionRef = db.collection(Constants.HOUSEHOLD_COLLECTION_REF)
    private val householdDocRef = householdsCollectionRef.document()

    fun createNewHousehold(onComplete: (householdID: String) -> Unit, onFailure: () -> Unit) {
        val householdID = householdDocRef.id

        val categories = GenerateData.eventCategories()
        for (category in categories) {
            val householdSubEventCategories =
                householdDocRef.collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF).document()
            category.categoryId = householdSubEventCategories.id
            householdSubEventCategories.set(category)
        }

        householdDocRef.set(Household(householdId = householdID))
            .addOnCompleteListener {
                onComplete.invoke(householdID)
            }.addOnFailureListener {
                onFailure.invoke()
            }
    }

    fun removeHousehold(householdId: String) {
        //delete sub items
        householdDocRef.collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF).document().delete()

        // delte household
        householdsCollectionRef
            .document(householdId)
            .delete()
    }

    companion object {
        private const val TAG = "TIMTIM"
    }
}
