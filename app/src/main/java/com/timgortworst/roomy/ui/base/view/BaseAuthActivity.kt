package com.timgortworst.roomy.ui.base.view

import android.net.Uri
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.R
import com.timgortworst.roomy.ui.googlesignin.view.GoogleSignInActivity
import dagger.android.AndroidInjection
import javax.inject.Inject


open class BaseAuthActivity : BaseActivity() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun logout() {
        firebaseAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            finishAffinity()
            GoogleSignInActivity.start(this)
        }
    }

    fun revokeAccess(): Task<Void>? {
        firebaseAuth.signOut()
        return googleSignInClient.revokeAccess().addOnCompleteListener {
            finishAffinity()
            GoogleSignInActivity.start(this)
        }
    }

    fun getProfileImage(): Uri? {
        return firebaseAuth.currentUser?.photoUrl
    }
}