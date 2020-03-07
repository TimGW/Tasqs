package com.timgortworst.roomy.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.presentation.RoomyApp
import kotlinx.coroutines.tasks.await

/**
 * Class to retrieve the household ID for the current signed in user.
 */
class IdProvider(
    private val sharedPrefs: SharedPrefs
) {
    private val userCollection = FirebaseFirestore
        .getInstance()
        .collection(User.USER_COLLECTION_REF)

    /**
     * retrieve the household ID for the current signed in user.
     * May be fetched locally or remote
     *
     * @return the users' household ID
     */
    suspend fun getHouseholdId(): String {
        return if (getLocalHouseholdId().isNotBlank()) {
            getLocalHouseholdId()
        } else {
            getRemoteHouseholdId()
        }
    }

    /**
     * Perform a User 'GET' request
     * @return the users' household ID
     */
    private suspend fun getRemoteHouseholdId(): String {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return ""
        val userDocRef = userCollection.document(userId)
        val user = try {
            userDocRef.get().await().toObject(User::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.e(RoomyApp.TAG, e.localizedMessage.orEmpty())
            null
        }
        sharedPrefs.setHouseholdId(user?.householdId.orEmpty())
        return user?.householdId.orEmpty()
    }

    /**
     * Retrieve the household ID from the local cache
     * @return the users' household ID
     */
    private fun getLocalHouseholdId() = sharedPrefs.getHouseholdId()
}
