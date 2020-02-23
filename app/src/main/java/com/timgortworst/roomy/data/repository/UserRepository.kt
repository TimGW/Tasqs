package com.timgortworst.roomy.data.repository

import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.domain.model.NetworkResponse
import com.timgortworst.roomy.domain.model.Role
import com.timgortworst.roomy.domain.model.UIState
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.User.Companion.USER_COLLECTION_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_EMAIL_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_NAME_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_ROLE_REF
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection(USER_COLLECTION_REF)
    private var registration: ListenerRegistration? = null

    fun getCurrentUserId() = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun createUser(householdId: String, fireBaseUser: FirebaseUser) {
        db.runTransaction { transition ->
            val currentUserDocRef = userCollection.document(fireBaseUser.uid)
            val userDoc = transition.get(currentUserDocRef)
            val newUser = User(
                    fireBaseUser.uid,
                    fireBaseUser.displayName ?: "",
                    fireBaseUser.email ?: "",
                    Role.ADMIN.name,
                    householdId)

            if (!userDoc.exists()) {
                transition.set(currentUserDocRef, newUser)
            }
        }.await()
    }

    suspend fun getUser(userId: String? = getCurrentUserId()): User? {
        if (userId.isNullOrEmpty()) return null

        val currentUserDocRef = userCollection.document(userId)
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
        val runnable = Runnable { apiStatus.setState(NetworkResponse.Loading) }
        handler.postDelayed(runnable, android.R.integer.config_shortAnimTime.toLong())

        registration = userCollection
                .whereEqualTo(USER_HOUSEHOLD_ID_REF, householdId)
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    handler.removeCallbacks(runnable)
                    Log.d(TAG, "isFromCache: ${snapshots?.metadata?.isFromCache}")
                    val result = when {
                        e != null && snapshots == null -> {
                            Log.e(TAG, "listen:error", e)
                            NetworkResponse.Error
                        }
                        else -> {
                            val documentChanges = snapshots?.documentChanges ?: return@EventListener
                            val totalDataSetSize = snapshots.documents.size
                            val result = mutableListOf<Pair<User, DocumentChange.Type>>()
                            documentChanges.forEach {
                                result.add(Pair(it.document.toObject(User::class.java), it.type))
                            }
                            NetworkResponse.HasData(result, totalDataSetSize, snapshots.metadata.hasPendingWrites())
                        }
                    }
                    apiStatus.setState(result)
                })
    }

    suspend fun getUserListForHousehold(householdId: String?): List<User>? {
        if (householdId.isNullOrEmpty()) return null

        return try {
            userCollection
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

        val userDocRef = userCollection.document(userId)
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
        val userDocRef = userCollection.document(userId)

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
