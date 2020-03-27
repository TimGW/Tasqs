package com.timgortworst.roomy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.User.Companion.USER_ADMIN_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_COLLECTION_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_TOKENS_REF
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore
) {
    private val userCollection = db.collection(USER_COLLECTION_REF)

    suspend fun createUser(
        householdId: String,
        fireBaseUser: FirebaseUser,
        registrationToken: String
    ) {
        db.runTransaction { transition ->
            val currentUserDocRef = userCollection.document(fireBaseUser.uid)
            val userDoc = transition.get(currentUserDocRef)
            val newUser = User(
                userId = fireBaseUser.uid,
                name = fireBaseUser.displayName ?: "",
                email = fireBaseUser.email ?: "",
                isAdmin = true,
                householdId = householdId,
                registrationTokens = mutableListOf(registrationToken)
            )

            if (!userDoc.exists()) {
                transition.set(currentUserDocRef, newUser)
            }
        }.await()
    }

    suspend fun getUser(userId: String?): User? {
        if (userId.isNullOrEmpty()) return null
        val currentUserDocRef = userCollection.document(userId)
        return currentUserDocRef.get().await().toObject(User::class.java)
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
        householdId: String? = null,
        isAdmin: Boolean? = null,
        tokens: MutableList<String>? = null
    ) {
        // don't update ID and name, since those are also used in tasks.
        // Otherwise implement cascading update for the tasks

        userId ?: return
        val userDocRef = userCollection.document(userId)

        val userFieldMap = mutableMapOf<String, Any>()
        householdId?.let { userFieldMap[USER_HOUSEHOLD_ID_REF] = it }
        isAdmin?.let { userFieldMap[USER_ADMIN_REF] = it }
        tokens?.let { userFieldMap[USER_TOKENS_REF] = it }

        userDocRef.set(userFieldMap, SetOptions.merge()).await()
    }

    suspend fun addUserToken(
        userId: String? = FirebaseAuth.getInstance().currentUser?.uid,
        token: String
    ) {
        userId ?: return
        val userDocRef = userCollection.document(userId)
        userDocRef.update(USER_TOKENS_REF, FieldValue.arrayUnion(token)).await()
    }

    suspend fun deleteUser(id: String) {
        userCollection.document(id).delete().await()
    }
}
