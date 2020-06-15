package com.timgortworst.roomy.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.User.Companion.USER_ADMIN_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_COLLECTION_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_TOKENS_REF
import com.timgortworst.roomy.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl : UserRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userCollection = db.collection(USER_COLLECTION_REF)

    @Throws(FirebaseFirestoreException::class)
    override suspend fun createUser(
        householdId: String,
        fireBaseUser: FirebaseUser,
        registrationToken: String
    ) {
        db.runTransaction { transition ->
            val currentUserDocRef = userCollection.document(fireBaseUser.uid)
            val userDoc = transition.get(currentUserDocRef)
            val newUser = User(
                userId = fireBaseUser.uid,
                name = fireBaseUser.displayName ?: "Unknown",
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

    @Throws(FirebaseFirestoreException::class)
    override suspend fun getUser(userId: String?, source: Source): User? {
        if (userId.isNullOrEmpty()) return null
        val currentUserDocRef = userCollection.document(userId)
        return currentUserDocRef.get().await().toObject(User::class.java)
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun getAllUsersForHousehold(id: String): List<User> {
        return userCollection
            .whereEqualTo(USER_HOUSEHOLD_ID_REF, id)
            .orderBy(USER_ADMIN_REF, Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(User::class.java)
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun updateUser(
        userId: String?,
        householdId: String?,
        isAdmin: Boolean?,
        tokens: MutableList<String>?
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

    @Throws(FirebaseFirestoreException::class)
    override suspend fun addUserToken(
        userId: String?,
        token: String
    ) {
        userId ?: return
        val userDocRef = userCollection.document(userId)
        userDocRef.update(USER_TOKENS_REF, FieldValue.arrayUnion(token)).await()
    }
}
