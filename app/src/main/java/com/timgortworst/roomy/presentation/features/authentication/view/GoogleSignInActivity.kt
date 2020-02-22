package com.timgortworst.roomy.presentation.features.authentication.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.timgortworst.roomy.R
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.authentication.presenter.GoogleSignInPresenter
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class GoogleSignInActivity : BaseActivity(), GoogleSignInView {
    private val presenter: GoogleSignInPresenter by inject {
        parametersOf(this)
    }

    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
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
                showProgressDialog()
                val account = task.getResult(ApiException::class.java) as GoogleSignInAccount
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                presenter.signInWithCredential(credential)
            } catch (e: ApiException) {
                setResult(Activity.RESULT_CANCELED, Intent())
                finish()
            }
        }
    }

    override fun loginSuccessful() {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }

    override fun loginFailed() {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
    }

    override fun failedInitUser() {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
    }

//    fun logout() {
//        firebaseAuth.signOut()
//        googleSignInClient.signOut().addOnCompleteListener(this) {
//            finishAffinity()
//            GoogleSignInActivity.startForResult(this)
//        }
//    }
//
//    fun revokeAccess(): Task<Void>? {
//        FirebaseAuth.getInstance().signOut()
//        return googleSignInClient.revokeAccess().addOnCompleteListener {
//            finishAffinity()
//        }
//    }

    companion object {
        private const val RC_SIGN_IN = 9001

        fun startForResult(originActivity: Activity, resultCode: Int) {
            val intent = Intent(originActivity, GoogleSignInActivity::class.java)
            originActivity.startActivityForResult(intent, resultCode)
        }
    }
}