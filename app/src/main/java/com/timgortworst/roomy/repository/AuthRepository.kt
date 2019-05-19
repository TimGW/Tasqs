package com.timgortworst.roomy.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import kotlinx.coroutines.tasks.await


class AuthRepository(
    private val db: FirebaseFirestore,
    private val sharedPref: HuishoudGenootSharedPref,
    private val auth: FirebaseAuth
) {

    companion object {
        private const val TAG = "TIMTIM"
    }

    fun getFirebaseUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun signIn(credential: AuthCredential): AuthResult? {
        return auth.signInWithCredential(credential).await()
    }

    fun signOut() {
        auth.signOut()
    }
}
