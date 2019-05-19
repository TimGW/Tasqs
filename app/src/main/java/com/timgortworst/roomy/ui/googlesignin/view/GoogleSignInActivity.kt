package com.timgortworst.roomy.ui.googlesignin.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.timgortworst.roomy.R
import com.timgortworst.roomy.ui.base.view.BaseAuthActivity
import com.timgortworst.roomy.ui.googlesignin.presenter.GoogleSignInPresenter
import com.timgortworst.roomy.ui.setup.view.SetupActivity
import com.timgortworst.roomy.ui.splash.ui.SplashActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


class GoogleSignInActivity : BaseAuthActivity(), GoogleSignInView {

    @Inject
    lateinit var presenter: GoogleSignInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
                Log.w(TAG, "Google sign in failed", e)
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

    companion object {
        private const val TAG = "SignInActivity"
        private const val RC_SIGN_IN = 9001

        fun start(context: Context) {
            val intent = Intent(context, GoogleSignInActivity::class.java)
            context.startActivity(intent)
        }
    }
}