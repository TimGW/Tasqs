package com.timgortworst.roomy.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.presentation.RoomyApp
import kotlinx.coroutines.tasks.await

/**
 * Class to retrieve the household ID for the current signed in user.
 */
class IdProvider {
    private val userCollection = FirebaseFirestore
        .getInstance()
        .collection(User.USER_COLLECTION_REF)

    /**
     * Perform a User 'GET' request
     * @return the users' household ID
     */
    suspend fun fetchHouseholdId(): String {
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
