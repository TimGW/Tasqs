package com.timgortworst.roomy.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.utils.Constants
import com.timgortworst.roomy.utils.Constants.AGENDA_EVENT_CATEGORIES_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.USERS_COLLECTION_REF
import com.timgortworst.roomy.utils.GenerateData
import kotlinx.coroutines.InternalCoroutinesApi



class HouseholdRepository(
    private val db: FirebaseFirestore,
    private val sharedPref: HuishoudGenootSharedPref,
    private val auth: FirebaseAuth
) {

    val householdsCollectionRef = db.collection(Constants.HOUSEHOLD_COLLECTION_REF)
    val usersCollectionRef = db.collection(Constants.USERS_COLLECTION_REF)

    fun isHouseholdInDb(householdId: String, onComplete: (isHouseholdInDb: Boolean) -> Unit) {
        if (householdId.isBlank()) {
            onComplete(false)
            return
        }

        householdsCollectionRef.document(householdId).get().addOnSuccessListener {
            sharedPref.setHouseholdId(householdId)
            onComplete(it.exists())
        }.addOnFailureListener { onComplete(false) }
    }

    fun createNewHouseholdAndUser(): Task<Transaction> {
        val currentUserDocRef = usersCollectionRef.document(auth.currentUser?.uid.orEmpty())
        val householdDocRef = householdsCollectionRef.document()
        val householdID = householdDocRef.id
        val firebaseUser = auth.currentUser
        val newUser = User(
            firebaseUser?.uid ?: "",
            firebaseUser?.displayName ?: "",
            firebaseUser?.email ?: "",
            0, // points
            User.Role.ADMIN.name,
            householdID)
        val subcollectionHouseholdUsers = householdsCollectionRef
            .document(householdID)
            .collection(USERS_COLLECTION_REF)
            .document(newUser.userId)

        return db.runTransaction { transaction ->
            // New household transaction
            val newHousehold = Household(householdID)
            sharedPref.setHouseholdId(householdID)
            transaction.set(householdDocRef, newHousehold)

            // New categories sub-collection in household transaction
            val categories = GenerateData.eventCategories()
            for (category in categories) {
                val householdSubEventCategories = householdDocRef.collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF).document()
                category.categoryId = householdSubEventCategories.id
                transaction.set(householdSubEventCategories, category)
            }

            // New user transaction
            transaction.set(currentUserDocRef, newUser)

            // new users sub-collection in household transaction
            // transaction.set(subcollectionHouseholdUsers, newUser)
        }
    }

    companion object {
        private const val TAG = "TIMTIM"
    }
}
