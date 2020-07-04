package com.timgortworst.tasqs.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.timgortworst.tasqs.data.mapper.ListMapper
import com.timgortworst.tasqs.data.mapper.Mapper
import com.timgortworst.tasqs.data.mapper.NullableOutputListMapper
import com.timgortworst.tasqs.data.mapper.UserDataMapper.Companion.USER_ADMIN_REF
import com.timgortworst.tasqs.data.mapper.UserDataMapper.Companion.USER_COLLECTION_REF
import com.timgortworst.tasqs.data.mapper.UserDataMapper.Companion.USER_HOUSEHOLD_ID_REF
import com.timgortworst.tasqs.data.mapper.UserDataMapper.Companion.USER_TOKENS_REF
import com.timgortworst.tasqs.domain.model.User
import com.timgortworst.tasqs.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val userDataMapper: Mapper<Map<String, Any>, User?>,
    private val userListMapper: NullableOutputListMapper<Map<String, Any>, User?>
) : UserRepository {
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
        val networkUser = currentUserDocRef.get().await().data.orEmpty()
        return userDataMapper.mapIncoming(networkUser)
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun getAllUsersForHousehold(id: String): List<User> {
        val networkUserList : List<Map<String, Any>> = userCollection
            .whereEqualTo(USER_HOUSEHOLD_ID_REF, id)
            .orderBy(USER_ADMIN_REF, Query.Direction.DESCENDING)
            .get()
            .await()
            .documents.mapNotNull { it.data }

        return userListMapper.mapIncoming(networkUserList)?.filterNotNull().orEmpty()
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun updateUser(
        userId: String?,
        householdId: String?,
        isAdmin: Boolean?,
        tokens: MutableList<String>?
    ) {
        // ! don't update ID and name, since those are also used in tasks.
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
