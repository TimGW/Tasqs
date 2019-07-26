package com.timgortworst.roomy.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.utils.Constants.USER_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.USER_EMAIL_REF
import com.timgortworst.roomy.utils.Constants.USER_HOUSEHOLDID_REF
import com.timgortworst.roomy.utils.Constants.USER_NAME_REF
import com.timgortworst.roomy.utils.Constants.USER_ROLE_REF
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    val userCollectionRef = FirebaseFirestore.getInstance().collection(USER_COLLECTION_REF)

    fun getCurrentUserId() = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun createUser() {
        val currentUserDocRef = userCollectionRef.document(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
        val userDoc = currentUserDocRef.get().await()
        if (!userDoc.exists()) {
            val newUser = User(
                    FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                    FirebaseAuth.getInstance().currentUser?.email ?: ""
            )
            currentUserDocRef.set(newUser).await()
        }
    }

    suspend fun getUser(userId: String? = getCurrentUserId()): User? {
        if (userId.isNullOrEmpty()) return null

        val currentUserDocRef = userCollectionRef.document(userId)
        return currentUserDocRef.get().await().toObject(User::class.java) as User
    }

    suspend fun getUserListForHousehold(householdId: String?): List<User>? {
        if (householdId.isNullOrEmpty()) return null

        return userCollectionRef
                .whereEqualTo(USER_HOUSEHOLDID_REF, householdId)
                .get()
                .await()
                .toObjects(User::class.java)
    }

    suspend fun getHouseholdIdForUser(userId: String? = getCurrentUserId()): String {
        if (userId.isNullOrEmpty()) return ""

        val userDocRef = userCollectionRef.document(userId)
        val user = userDocRef.get().await().toObject(User::class.java) as User
        return user.householdId
    }

    suspend fun updateUser(
            userId: String = FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
            name: String = "",
            email: String = "",
            householdId: String = "",
            role: String = ""
    ) {
        val userDocRef = userCollectionRef.document(userId)

        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap[USER_NAME_REF] = name
        if (email.isNotBlank()) userFieldMap[USER_EMAIL_REF] = email
        if (householdId.isNotBlank()) userFieldMap[USER_HOUSEHOLDID_REF] = householdId
        if (role.isNotBlank()) userFieldMap[USER_ROLE_REF] = role

        userDocRef.set(userFieldMap, SetOptions.merge()).await()
    }
}
