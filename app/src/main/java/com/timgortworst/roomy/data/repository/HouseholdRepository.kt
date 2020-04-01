package com.timgortworst.roomy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.firestore.Household
import com.timgortworst.roomy.domain.model.firestore.Household.Companion.HOUSEHOLD_COLLECTION_REF
import com.timgortworst.roomy.domain.model.firestore.User
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import kotlinx.coroutines.tasks.await

class HouseholdRepository(
    db: FirebaseFirestore
) {
    private val householdCollection = db.collection(HOUSEHOLD_COLLECTION_REF)
    private val userCollection = db.collection(User.USER_COLLECTION_REF)

    @Throws(FirebaseFirestoreException::class)
    suspend fun taskCollection(): CollectionReference {
        return householdCollection
            .document(getHouseholdId())
            .collection(TaskJson.TASK_COLLECTION_REF)
    }

    @Throws(FirebaseFirestoreException::class)
    suspend fun createHousehold(): String {
        val household = householdCollection.document()
        household.set(
            Household(
                householdId = household.id
            )
        ).await()
        return household.id
    }

    @Throws(FirebaseFirestoreException::class)
    suspend fun deleteHousehold(householdId: String) {
        householdCollection.document(householdId).delete().await()
    }

    /**
     * Perform a User 'GET' request todo cache?
     * @return the users' household ID
     */
    @Throws(FirebaseFirestoreException::class)
    suspend fun getHouseholdId(): String {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return ""
        val userDocRef = userCollection.document(userId)
        val user = userDocRef.get().await().toObject(User::class.java)
        return user?.householdId.orEmpty()
    }
}
