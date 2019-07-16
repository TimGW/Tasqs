package com.timgortworst.roomy.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.utils.Constants.USERS_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.USER_EMAIL_REF
import com.timgortworst.roomy.utils.Constants.USER_HOUSEHOLDID_REF
import com.timgortworst.roomy.utils.Constants.USER_NAME_REF
import com.timgortworst.roomy.utils.Constants.USER_ROLE_REF
import kotlinx.coroutines.tasks.await

class UserRepository(
    db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    val userCollectionRef = db.collection(USERS_COLLECTION_REF)

    companion object {
        private const val TAG = "TIMTIM"
    }

    fun getCurrentUserId() = auth.currentUser?.uid

    suspend fun getOrCreateUser(): User? {
        val currentUserDocRef = userCollectionRef.document(auth.currentUser?.uid.orEmpty())

        return try {
            val userDoc = currentUserDocRef.get().await()

            return if (!userDoc.exists()) {
                val newUser = User(
                    auth.currentUser?.uid ?: "",
                    auth.currentUser?.displayName ?: "",
                    auth.currentUser?.email ?: ""
                )

                try {
                    currentUserDocRef.set(newUser).await()
                    newUser
                } catch (e: FirebaseFirestoreException) {
                    null
                }
            } else {
                userDoc.toObject(User::class.java)
            }
        } catch (e: FirebaseFirestoreException) {
            null
        }
    }

    suspend fun setOrUpdateUser(
        userId: String = auth.currentUser?.uid.orEmpty(),
        name: String = "",
        email: String = "",
        householdId: String = "",
        role: String = ""
    ) {
        val currentUserDocRef = userCollectionRef.document(userId)

        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap[USER_NAME_REF] = name
        if (email.isNotBlank()) userFieldMap[USER_EMAIL_REF] = email
        if (householdId.isNotBlank()) userFieldMap[USER_HOUSEHOLDID_REF] = householdId
        if (role.isNotBlank()) userFieldMap[USER_ROLE_REF] = role

        currentUserDocRef.set(userFieldMap, SetOptions.merge()).await()
    }

    suspend fun getUsersForHouseholdId(householdId: String): List<User> {
        return userCollectionRef
            .whereEqualTo(USER_HOUSEHOLDID_REF, householdId)
            .get()
            .await()
            .toObjects(User::class.java)
    }

    suspend fun deleteUser(user: User) {
        userCollectionRef.document(user.userId).delete().await()
//        userCollectionRef.document(user.userId).update(USER_HOUSEHOLDID_REF, "").await()
        // todo remove agenda events where userId is assigned
    }

    suspend fun getHouseholdIdForCurrentUser(): String {
        val currentUserDocRef = userCollectionRef.document(auth.currentUser?.uid.orEmpty())
        val userDoc = currentUserDocRef.get().await()
        val user = userDoc.toObject(User::class.java) as User
        return user.householdId
    }
}
