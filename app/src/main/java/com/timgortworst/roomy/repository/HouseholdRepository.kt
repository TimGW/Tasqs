package com.timgortworst.roomy.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.utils.Constants
import com.timgortworst.roomy.utils.Constants.AGENDA_EVENT_CATEGORIES_COLLECTION_REF
import com.timgortworst.roomy.utils.GenerateData


class HouseholdRepository(
    private val db: FirebaseFirestore,
    private val sharedPref: HuishoudGenootSharedPref,
    private val auth: FirebaseAuth
) {
    val householdsCollectionRef = db.collection(Constants.HOUSEHOLD_COLLECTION_REF)

//    fun isHouseholdInDb(householdId: String, onComplete: (isHouseholdInDb: Boolean) -> Unit) {
//        householdsCollectionRef.document(householdId).get().addOnSuccessListener {
//            onComplete(it.exists())
//        }.addOnFailureListener { onComplete(false) }
//    }

    fun createNewHousehold(onComplete: (householdID : String) -> Unit, onFailure: () -> Unit) {
        val householdDocRef = householdsCollectionRef.document()
        val householdID = householdDocRef.id

        sharedPref.setHouseholdId(householdID)

        val categories = GenerateData.eventCategories()
        for (category in categories) {
            val householdSubEventCategories =
                householdDocRef.collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF).document()
            category.categoryId = householdSubEventCategories.id
            householdSubEventCategories.set(category)
        }

        householdDocRef.set(Household(householdID))
            .addOnCompleteListener {
                onComplete.invoke(householdID)
            }.addOnFailureListener {
                onFailure.invoke()
            }
    }

    companion object {
        private const val TAG = "TIMTIM"
    }
}
