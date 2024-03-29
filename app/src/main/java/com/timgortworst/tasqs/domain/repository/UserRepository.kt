package com.timgortworst.tasqs.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.timgortworst.tasqs.domain.model.User

interface UserRepository {
    @Throws(FirebaseFirestoreException::class)
    suspend fun createUser(
        householdId: String,
        fireBaseUser: FirebaseUser
    )

    @Throws(FirebaseFirestoreException::class)
    suspend fun getUser(
        userId: String? = FirebaseAuth.getInstance().currentUser?.uid,
        source: Source = Source.DEFAULT
    ): User?

    @Throws(FirebaseFirestoreException::class)
    suspend fun getAllUsersForHousehold(id: String): List<User>

    @Throws(FirebaseFirestoreException::class)
    suspend fun updateUser(
        userId: String? = FirebaseAuth.getInstance().currentUser?.uid,
        householdId: String? = null,
        isAdmin: Boolean? = null
    )
}
