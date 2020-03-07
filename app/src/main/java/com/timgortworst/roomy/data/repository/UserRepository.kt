package com.timgortworst.roomy.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.domain.model.Role
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.User.Companion.USER_COLLECTION_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_EMAIL_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_NAME_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_ROLE_REF
import com.timgortworst.roomy.presentation.RoomyApp.Companion.TAG
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val idProvider: IdProvider
) {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection(USER_COLLECTION_REF)

    suspend fun createUser(householdId: String, fireBaseUser: FirebaseUser) {
        db.runTransaction { transition ->
            val currentUserDocRef = userCollection.document(fireBaseUser.uid)
            val userDoc = transition.get(currentUserDocRef)
            val newUser = User(
                fireBaseUser.uid,
                fireBaseUser.displayName ?: "",
                fireBaseUser.email ?: "",
                Role.ADMIN.name,
                householdId
            )

            if (!userDoc.exists()) {
                transition.set(currentUserDocRef, newUser)
            }
        }.await()
    }

    suspend fun getUser(userId: String?): User? {
        if (userId.isNullOrEmpty()) return null

        val currentUserDocRef = userCollection.document(userId)
        return try {
            currentUserDocRef.get().await().toObject(User::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            null
        }
    }

    suspend fun allUsersQuery(): Query {
        return userCollection
            .whereEqualTo(USER_HOUSEHOLD_ID_REF, idProvider.getHouseholdId())
    }

    suspend fun getAllUsers(): List<User>? {
        if (idProvider.getHouseholdId().isEmpty()) return null

        return try {
            userCollection
                .whereEqualTo(USER_HOUSEHOLD_ID_REF, idProvider.getHouseholdId())
                .get()
                .await()
                .toObjects(User::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            null
        }
    }

    suspend fun updateUser(
        userId: String? = FirebaseAuth.getInstance().currentUser?.uid,
        name: String? = null,
        email: String? = null,
        householdId: String? = null,
        role: String? = null
    ) {
        userId ?: return
        val userDocRef = userCollection.document(userId)

        val userFieldMap = mutableMapOf<String, Any>()
        name?.let { userFieldMap[USER_NAME_REF] = it }
        email?.let { userFieldMap[USER_EMAIL_REF] = it }
        householdId?.let { userFieldMap[USER_HOUSEHOLD_ID_REF] = it }
        role?.let { userFieldMap[USER_ROLE_REF] = it }

        try {
            userDocRef.set(userFieldMap, SetOptions.merge()).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun deleteUser(
        id: String
    ) {
        try {
            userCollection
                .document(id)
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }
}
