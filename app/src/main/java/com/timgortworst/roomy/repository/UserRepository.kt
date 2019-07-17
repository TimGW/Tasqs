package com.timgortworst.roomy.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.utils.Constants.USERS_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.USER_EMAIL_REF
import com.timgortworst.roomy.utils.Constants.USER_HOUSEHOLDID_REF
import com.timgortworst.roomy.utils.Constants.USER_NAME_REF
import com.timgortworst.roomy.utils.Constants.USER_ROLE_REF
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userCollectionRef = db.collection(USERS_COLLECTION_REF)

    fun getCurrentUserId() = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun createNewUser() {
        val currentUserDocRef = userCollectionRef.document(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
        val userDoc = currentUserDocRef.get().await()
        if (!userDoc.exists()) {
            val newUser = User(
                    FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                    FirebaseAuth.getInstance().currentUser?.email ?: "")
            currentUserDocRef.set(newUser).await()
        }
    }

    suspend fun getCurrentUser(): User {
        val currentUserDocRef = userCollectionRef.document(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
        return currentUserDocRef.get().await().toObject(User::class.java) as User
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

    suspend fun getUsersForHouseholdId(householdId: String): List<User> {
        return userCollectionRef
                .whereEqualTo(USER_HOUSEHOLDID_REF, householdId)
                .get()
                .await()
                .toObjects(User::class.java)
    }

    suspend fun deleteUser(user: User) {
        userCollectionRef.document(user.userId).update(USER_HOUSEHOLDID_REF, "").await()
        // todo remove agenda events where userId is assigned
    }

    suspend fun getHouseholdIdForCurrentUser(): String {
        val currentUserDocRef = userCollectionRef.document(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
        val user = currentUserDocRef.get().await().toObject(User::class.java) as User
        return user.householdId
    }
}
