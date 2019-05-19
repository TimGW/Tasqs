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
import com.timgortworst.roomy.utils.Constants.USER_TOTALPOINTS_REF
import kotlinx.coroutines.tasks.await

class UserRepository(
    db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    val userCollectionRef = db.collection(USERS_COLLECTION_REF)

    companion object {
        private const val TAG = "TIMTIM"
    }

    suspend fun getOrCreateUser(): User? {
        val currentUserDocRef = userCollectionRef.document(auth.currentUser?.uid.orEmpty())

        return try {
            val userDoc = currentUserDocRef.get().await()

            return if (!userDoc.exists()) {
                val newUser = User(
                    auth.currentUser?.uid ?: "",
                    auth.currentUser?.displayName ?: "",
                    auth.currentUser?.email ?: "")

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

    fun setOrUpdateUser(
        userId: String = auth.currentUser?.uid.orEmpty(),
        name: String = "",
        email: String = "",
        totalPoints: Int = 0,
        householdId: String = "",
        role: String = "",
        onComplete: () -> Unit,
        onFailure: () -> Unit
    ) {
        val currentUserDocRef = userCollectionRef.document(userId)

        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap[USER_NAME_REF] = name
        if (email.isNotBlank()) userFieldMap[USER_EMAIL_REF] = email
        if (totalPoints != 0) userFieldMap[USER_TOTALPOINTS_REF] = totalPoints
        if (householdId.isNotBlank()) userFieldMap[USER_HOUSEHOLDID_REF] = householdId
        if (role.isNotBlank()) userFieldMap[USER_ROLE_REF] = role

        currentUserDocRef.set(userFieldMap, SetOptions.merge())
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { onFailure() }
    }

    fun getUsersForHouseholdId(householdId: String, onComplete: (List<User>) -> Unit, onFailure: () -> Unit) {
        val query = userCollectionRef.whereEqualTo(USER_HOUSEHOLDID_REF, householdId)
        query.get().addOnSuccessListener { snapshot ->
            onComplete(snapshot.toObjects(User::class.java))
        }.addOnFailureListener {
            onFailure()
        }
    }
}
