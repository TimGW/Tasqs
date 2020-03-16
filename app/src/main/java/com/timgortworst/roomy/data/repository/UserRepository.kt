package com.timgortworst.roomy.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.User.Companion.USER_ADMIN_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_COLLECTION_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_EMAIL_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_NAME_REF
import com.timgortworst.roomy.presentation.RoomyApp.Companion.TAG
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore
) {
    private val userCollection = db.collection(USER_COLLECTION_REF)

    suspend fun createUser(householdId: String, fireBaseUser: FirebaseUser) {
        db.runTransaction { transition ->
            val currentUserDocRef = userCollection.document(fireBaseUser.uid)
            val userDoc = transition.get(currentUserDocRef)
            val newUser = User(
                userId = fireBaseUser.uid,
                name = fireBaseUser.displayName ?: "",
                email = fireBaseUser.email ?: "",
                isAdmin = true,
                householdId = householdId
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

    suspend fun getAllUsersForHousehold(id: String): List<User> {
        return userCollection
            .whereEqualTo(USER_HOUSEHOLD_ID_REF, id)
            .orderBy(USER_ADMIN_REF, Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(User::class.java)
    }

    suspend fun updateUser(
        userId: String? = FirebaseAuth.getInstance().currentUser?.uid,
        name: String? = null,
        email: String? = null,
        householdId: String? = null,
        isAdmin: Boolean? = null
    ) {
        userId ?: return
        val userDocRef = userCollection.document(userId)

        val userFieldMap = mutableMapOf<String, Any>()
        name?.let { userFieldMap[USER_NAME_REF] = it }
        email?.let { userFieldMap[USER_EMAIL_REF] = it }
        householdId?.let { userFieldMap[USER_HOUSEHOLD_ID_REF] = it }
        isAdmin?.let { userFieldMap[USER_ADMIN_REF] = it }

        userDocRef.set(userFieldMap, SetOptions.merge()).await()
    }

    suspend fun deleteUser(id: String) {
        userCollection.document(id).delete().await()
    }
}
