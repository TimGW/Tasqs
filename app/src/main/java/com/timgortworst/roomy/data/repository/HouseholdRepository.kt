package com.timgortworst.roomy.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.Household
import com.timgortworst.roomy.domain.model.Household.Companion.HOUSEHOLD_COLLECTION_REF
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.presentation.RoomyApp
import kotlinx.coroutines.tasks.await

class HouseholdRepository(
    db: FirebaseFirestore
) {
    private val householdCollection = db.collection(HOUSEHOLD_COLLECTION_REF)
    private val userCollection = db.collection(User.USER_COLLECTION_REF)

    suspend fun taskCollection(): CollectionReference {
        return householdCollection
            .document(getHouseholdId())
            .collection(TaskJson.TASK_COLLECTION_REF)
    }

    suspend fun createHousehold(): String {
        val household = householdCollection.document()
        household.set(Household(householdId = household.id)).await()
        return household.id
    }

    suspend fun deleteHousehold(householdId: String) {
        householdCollection.document(householdId).delete().await()
    }

    /**
     * Perform a User 'GET' request
     * @return the users' household ID
     */
    suspend fun getHouseholdId(): String {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return ""
        val userDocRef = userCollection.document(userId)
        val user = try {
            userDocRef.get().await().toObject(User::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.e(RoomyApp.TAG, e.localizedMessage.orEmpty())
            null
        }
        return user?.householdId.orEmpty()
    }
}
