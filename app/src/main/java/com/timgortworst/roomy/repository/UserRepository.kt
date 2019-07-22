package com.timgortworst.roomy.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.utils.Constants
import com.timgortworst.roomy.utils.Constants.USERS_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.USER_EMAIL_REF
import com.timgortworst.roomy.utils.Constants.USER_HOUSEHOLDID_REF
import com.timgortworst.roomy.utils.Constants.USER_NAME_REF
import com.timgortworst.roomy.utils.Constants.USER_ROLE_REF
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val householdRepository: HouseholdRepository) {
    private val db = FirebaseFirestore.getInstance()
    private val householdsCollectionRef = FirebaseFirestore.getInstance().collection(Constants.HOUSEHOLD_COLLECTION_REF)
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
        // set user on blacklist
        addUserToBlackList(user.userId)

        // remove id from user document
        userCollectionRef.document(user.userId).update(USER_HOUSEHOLDID_REF, "").await()

        // remove all events for that user
        removeEventsForUser(user.userId)

        // todo return error state or success
    }

    private suspend fun addUserToBlackList(userId: String) {
        val householdRef = householdsCollectionRef.document(getHouseholdIdForUser(userId))
        val household = householdRef.get().await().toObject(Household::class.java) as Household
        household.blackList.add(userId)
        householdRepository.updateHousehold(household.householdId, household.blackList)
    }

    private suspend fun removeEventsForUser(userId: String) {
//        try { todo
//            householdCollectionRef
//                .document(userRepository.getHouseholdIdForCurrentUser())
//                .collection(EVENT_COLLECTION_REF)
//                    .document()
//                    .whereEqualTo(EVENT_USER_REF, userId)
//                .delete()
//                .await()
//        } catch (e: FirebaseFirestoreException) {
//            Log.e("TIMTIM", e.localizedMessage!!)
//        }
    }

    suspend fun getHouseholdIdForUser(userId: String): String {
        val currentUserDocRef = userCollectionRef.document(userId)
        val user = currentUserDocRef.get().await().toObject(User::class.java) as User
        return user.householdId
    }

    suspend fun getHouseholdIdForCurrentUser(): String {
        val currentUserDocRef = userCollectionRef.document(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
        val user = currentUserDocRef.get().await().toObject(User::class.java) as User
        return user.householdId
    }
}
