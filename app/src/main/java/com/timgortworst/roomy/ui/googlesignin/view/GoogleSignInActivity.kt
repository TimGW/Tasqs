package com.timgortworst.roomy.ui.googlesignin.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.timgortworst.roomy.R
import com.timgortworst.roomy.ui.BaseActivity
import com.timgortworst.roomy.ui.googlesignin.presenter.GoogleSignInPresenter
import com.timgortworst.roomy.ui.setup.view.SetupActivity
import com.timgortworst.roomy.utils.showToast
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


class GoogleSignInActivity : BaseActivity(), GoogleSignInView {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var presenter: GoogleSignInPresenter

    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java) as GoogleSignInAccount
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                showToast("Google sign in failed")

                hideProgressDialog()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        showProgressDialog()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        presenter.signInWithCredential(credential)
    }

    override fun loginSuccessful() {
        hideProgressDialog()
        SetupActivity.start(this)
        finish()
    }

    override fun loginFailed() {
        showToast("login failed")
    }

    override fun failedInitUser() {
        showToast("failedInitUser")
    }

//    fun logout() {
//        firebaseAuth.signOut()
//        googleSignInClient.signOut().addOnCompleteListener(this) {
//            finishAffinity()
//            GoogleSignInActivity.start(this)
//        }
//    }
//
//    fun revokeAccess(): Task<Void>? {
//        firebaseAuth.signOut()
//        return googleSignInClient.revokeAccess().addOnCompleteListener {
//            finishAffinity()
//            GoogleSignInActivity.start(this)
//        }
//    }

    companion object {
        private const val TAG = "SignInActivity"
        private const val RC_SIGN_IN = 9001

        fun start(context: Context) {
            val intent = Intent(context, GoogleSignInActivity::class.java)
            context.startActivity(intent)
        }
    }
}