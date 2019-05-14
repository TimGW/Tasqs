package com.timgortworst.roomy.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.utils.Constants.USERS_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.USER_EMAIL_REF
import com.timgortworst.roomy.utils.Constants.USER_HOUSEHOLDID_REF
import com.timgortworst.roomy.utils.Constants.USER_NAME_REF
import com.timgortworst.roomy.utils.Constants.USER_ROLE_REF
import com.timgortworst.roomy.utils.Constants.USER_TOTALPOINTS_REF
import kotlinx.coroutines.InternalCoroutinesApi


class UserRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val sharedPref: HuishoudGenootSharedPref
) {
    val userCollectionRef = db.collection(USERS_COLLECTION_REF)

    companion object {
        private const val TAG = "TIMTIM"
    }

    fun getOrCreateUser(onComplete: (User) -> Unit) {
        val currentUserDocRef = userCollectionRef.document(auth.currentUser?.uid.orEmpty())
        currentUserDocRef.get().addOnSuccessListener { userDoc ->
            if (!userDoc.exists()) {
                val firebaseUser = auth.currentUser

                val newUser = User(
                    firebaseUser?.uid ?: "",
                    firebaseUser?.displayName ?: "",
                    firebaseUser?.email ?: "",
                    0,
                    User.Role.USER.name)

                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete(newUser)
                }.addOnFailureListener {
                    Log.e("TIMTIM", it.message) // todo fix permissiosn
                }
            } else {
                onComplete(userDoc.toObject(User::class.java)!!)
            }
        }
    }

    fun getUser(onComplete: (User?) -> Unit) {
        val currentUserDocRef = userCollectionRef.document(auth.currentUser?.uid.orEmpty())
        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java))
        }
    }

    fun setUser(user : User, onComplete: () -> Unit){
        val currentUserDocRef = userCollectionRef.document(user.userId)
        currentUserDocRef.set(user).addOnSuccessListener { onComplete() }
    }

    fun updateUser(
        name: String = "",
        email: String = "",
        totalPoints: Int = 0,
        householdId: String = "",
        role: String = "",
        onComplete: () -> Unit
    ) {
        val currentUserDocRef = userCollectionRef.document(auth.currentUser?.uid.orEmpty())

        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap[USER_NAME_REF] = name
        if (email.isNotBlank()) userFieldMap[USER_EMAIL_REF] = email
        if (totalPoints != 0) userFieldMap[USER_TOTALPOINTS_REF] = totalPoints
        if (householdId.isNotBlank()) userFieldMap[USER_HOUSEHOLDID_REF] = householdId
        if (role.isNotBlank()) userFieldMap[USER_ROLE_REF] = role

        currentUserDocRef.update(userFieldMap).addOnSuccessListener { onComplete() }
    }

    fun getUsersForHousehold(userListener: UserListener) {
        val query = userCollectionRef.whereEqualTo(USER_HOUSEHOLDID_REF, sharedPref.getHouseholdId())
        query.get().addOnSuccessListener { snapshot ->
            userListener.provideUserList(snapshot.toObjects(User::class.java))
        }
    }

    interface UserListener {
        fun provideUserList(users: MutableList<User>)
    }
}
