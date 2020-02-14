package com.timgortworst.roomy.data.repository

import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.data.model.User.Companion.USER_COLLECTION_REF
import com.timgortworst.roomy.data.model.User.Companion.USER_EMAIL_REF
import com.timgortworst.roomy.data.model.User.Companion.USER_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.data.model.User.Companion.USER_NAME_REF
import com.timgortworst.roomy.data.model.User.Companion.USER_ROLE_REF
import com.timgortworst.roomy.data.utils.Constants.LOADING_SPINNER_DELAY
import com.timgortworst.roomy.domain.Response
import com.timgortworst.roomy.domain.UIState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private val userCollectionRef = FirebaseFirestore.getInstance().collection(USER_COLLECTION_REF)

    private var registration: ListenerRegistration? = null

    fun getCurrentUserId() = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun createUser() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val currentUserDocRef = userCollectionRef.document(currentUserID)

        val userDoc = try {
            currentUserDocRef.get().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            null
        }

        if (userDoc?.exists() == false) {
            val newUser = User(
                    FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                    FirebaseAuth.getInstance().currentUser?.email ?: ""
            )

            try {
                currentUserDocRef.set(newUser).await()
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, e.localizedMessage.orEmpty())
            }
        }
    }

    suspend fun getUser(userId: String? = getCurrentUserId()): User? {
        if (userId.isNullOrEmpty()) return null

        val currentUserDocRef = userCollectionRef.document(userId)
        return try {
            currentUserDocRef.get().await().toObject(User::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            null
        }
    }

    fun listenToUsersForHousehold(householdId: String?, apiStatus: UIState<User>) {
        if (householdId.isNullOrEmpty()) return

        val handler = Handler()
        val runnable = Runnable { apiStatus.setState(Response.Loading) }
        handler.postDelayed(runnable, LOADING_SPINNER_DELAY)

        registration = userCollectionRef
                .whereEqualTo(USER_HOUSEHOLD_ID_REF, householdId)
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    handler.removeCallbacks(runnable)
                    Log.d(TAG, "isFromCache: ${snapshots?.metadata?.isFromCache}")
                    val result = when {
                        e != null && snapshots == null -> {
                            Log.e(TAG, "listen:error", e)
                            Response.Error
                        }
                        else -> {
                            val documentChanges = snapshots?.documentChanges ?: return@EventListener
                            val totalDataSetSize = snapshots.documents.size
                            val result = mutableListOf<Pair<User, DocumentChange.Type>>()
                            documentChanges.forEach {
                                result.add(Pair(it.document.toObject(User::class.java), it.type))
                            }
                            Response.HasData(result, totalDataSetSize, snapshots.metadata.hasPendingWrites())
                        }
                    }
                    apiStatus.setState(result)
                })
    }

    suspend fun getUserListForHousehold(householdId: String?): List<User>? {
        if (householdId.isNullOrEmpty()) return null

        return try {
            userCollectionRef
                    .whereEqualTo(USER_HOUSEHOLD_ID_REF, householdId)
                    .get()
                    .await()
                    .toObjects(User::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            null
        }
    }

    suspend fun getHouseholdIdForUser(userId: String? = getCurrentUserId()): String {
        if (userId.isNullOrEmpty()) return ""

        val userDocRef = userCollectionRef.document(userId)
        val user = try {
            userDocRef.get().await().toObject(User::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            null
        }

        return user?.householdId.orEmpty()
    }

    suspend fun updateUser(
            userId: String? = getCurrentUserId(),
            name: String? = null,
            email: String? = null,
            householdId: String? = null,
            role: String? = null
    ) {
        userId ?: return
        val userDocRef = userCollectionRef.document(userId)

        val userFieldMap = mutableMapOf<String, Any>()
        name?.let { userFieldMap[USER_NAME_REF] = it }
        email?.let { userFieldMap[USER_EMAIL_REF] = it }
        householdId?.let { userFieldMap[USER_HOUSEHOLD_ID_REF] = it }
        role?.let { userFieldMap[USER_ROLE_REF] = it }

        try {
            userDocRef.set(userFieldMap, SetOptions.merge()).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    fun detachUserListener() {
        registration?.remove()
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}
