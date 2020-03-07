package com.timgortworst.roomy.presentation.features.signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.timgortworst.roomy.BuildConfig
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.utils.showSnackbar
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.main.MainActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SignInActivity : BaseActivity(), SignInView {
    private val signInPresenter: SignInPresenter by inject { parametersOf(this) }

    companion object {
        private const val RC_SIGN_IN = 123

        fun start(context: Context) {
            context.startActivity(Intent(context, SignInActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_launcher_foreground)
                .setTheme(R.style.MyTheme_NoActionBar)
                .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK && response != null) {
                signInPresenter.handleLoginResult(response)
            } else {
                when {
                    response == null -> {
                        loginFailed(R.string.sign_in_cancelled)
                    }
                    response.error?.errorCode == ErrorCodes.NO_NETWORK -> {
                        loginFailed(R.string.error_connection)
                    }
                    else -> {
                        loginFailed(R.string.error_generic)
                    }
                }
            }
        } else {
            loginFailed(R.string.error_generic)
        }
    }

    override fun loginSuccessful() {
        MainActivity.start(this)
        finish()
    }

    override fun welcomeBack() {
        val content = findViewById<View>(android.R.id.content)
        content.showSnackbar(R.string.welcome_back)
        MainActivity.start(this)
        finish()
    }

    override fun loginFailed(errorMessage: Int) {
        val content = findViewById<View>(android.R.id.content)
        content.showSnackbar(errorMessage)
        finishAffinity()
    }
}