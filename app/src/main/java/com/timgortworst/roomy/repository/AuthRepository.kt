package com.timgortworst.roomy.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import kotlinx.coroutines.InternalCoroutinesApi



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

    fun signIn(credential: AuthCredential): Task<AuthResult> {
        return auth.signInWithCredential(credential)
    }

    fun signOut() {
        auth.signOut()
    }
}
