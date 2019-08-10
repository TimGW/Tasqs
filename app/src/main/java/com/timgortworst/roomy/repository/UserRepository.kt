package com.timgortworst.roomy.repository

import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.utils.Constants.LOADING_SPINNER_DELAY
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

    private var registration: ListenerRegistration? = null

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

    fun listenToUsersForHousehold(householdId: String?, baseResponse: BaseResponse) {
        if (householdId.isNullOrEmpty()) return

        val handler = Handler()
        val runnable = Runnable { baseResponse.setResponse(DataListener.Loading) }
        handler.postDelayed(runnable, LOADING_SPINNER_DELAY)

        registration = userCollectionRef
                .whereEqualTo(USER_HOUSEHOLDID_REF, householdId)
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    handler.removeCallbacks(runnable)
                    Log.d(TAG, "isFromCache: ${snapshots?.metadata?.isFromCache}")
                    when {
                        e != null && snapshots == null -> {
                            baseResponse.setResponse(DataListener.Error(e))
                            Log.w(TAG, "listen:error", e)
                        }
                        else -> {
                            val changeList = snapshots?.documentChanges?.toList() ?: return@EventListener
                            val totalDataSetSize = snapshots.documents.toList().size
                            baseResponse.setResponse(DataListener.Success(changeList, totalDataSetSize, snapshots.metadata.hasPendingWrites()))
                        }
                    }
                })
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
    ): String {
        val userDocRef = userCollectionRef.document(userId)

        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap[USER_NAME_REF] = name
        if (email.isNotBlank()) userFieldMap[USER_EMAIL_REF] = email
        if (householdId.isNotBlank()) userFieldMap[USER_HOUSEHOLDID_REF] = householdId
        if (role.isNotBlank()) userFieldMap[USER_ROLE_REF] = role

        userDocRef.set(userFieldMap, SetOptions.merge()).await()

        return userId
    }

    fun detachUserListener() {
        registration?.remove()
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}
