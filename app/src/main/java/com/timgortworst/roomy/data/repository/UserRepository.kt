package com.timgortworst.roomy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.timgortworst.roomy.domain.model.firestore.User
import com.timgortworst.roomy.domain.model.firestore.User.Companion.USER_ADMIN_REF
import com.timgortworst.roomy.domain.model.firestore.User.Companion.USER_COLLECTION_REF
import com.timgortworst.roomy.domain.model.firestore.User.Companion.USER_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.domain.model.firestore.User.Companion.USER_TOKENS_REF
import kotlinx.coroutines.tasks.await

interface UserRepository {
    @Throws(FirebaseFirestoreException::class)
    suspend fun createUser(
        householdId: String,
        fireBaseUser: FirebaseUser,
        registrationToken: String)

    @Throws(FirebaseFirestoreException::class)
    suspend fun getUser(userId: String? = FirebaseAuth.getInstance().currentUser?.uid,
                        source: Source = Source.DEFAULT): User?

    @Throws(FirebaseFirestoreException::class)
    suspend fun getAllUsersForHousehold(id: String): List<User>

    @Throws(FirebaseFirestoreException::class)
    suspend fun updateUser(
        userId: String? = FirebaseAuth.getInstance().currentUser?.uid,
        householdId: String? = null,
        isAdmin: Boolean? = null,
        tokens: MutableList<String>? = null)

    @Throws(FirebaseFirestoreException::class)
    suspend fun addUserToken(
        userId: String? = FirebaseAuth.getInstance().currentUser?.uid,
        token: String)
}
