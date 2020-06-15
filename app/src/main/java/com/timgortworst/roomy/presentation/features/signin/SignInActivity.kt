package com.timgortworst.roomy.presentation.features.signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.timgortworst.roomy.R
import com.timgortworst.roomy.presentation.base.model.SignInAction
import com.timgortworst.roomy.presentation.base.toast
import com.timgortworst.roomy.presentation.features.main.MainActivity
import org.koin.android.viewmodel.ext.android.viewModel

class SignInActivity : AppCompatActivity() {
    private val viewModel: SignInViewModel by viewModel()

    companion object {
        private const val RC_SIGN_IN = 123

        fun intentBuilder(context: Context): Intent {
            return Intent(context, SignInActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.action.observe(this@SignInActivity, Observer {
            when (it) {
                SignInAction.MainActivity -> loginSuccessful()
                is SignInAction.WelcomeBack -> loginSuccessful(it.userName)
                is SignInAction.Failed -> loginFailed(it.errorMsg)
            }
        })

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
                .setIsSmartLockEnabled(false, true)
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
            handleSignInResponse(resultCode, data)
        }
    }

    private fun handleSignInResponse(resultCode: Int, data: Intent?) {
        val response = IdpResponse.fromResultIntent(data)

        if (resultCode == Activity.RESULT_OK) {
            response?.let { viewModel.handleLoginResult(it) }
        } else {
            when {
                response == null -> loginFailed(R.string.sign_in_cancelled)
                response.error?.errorCode == ErrorCodes.NO_NETWORK ->
                    loginFailed(R.string.error_connection)
                else -> loginFailed(R.string.error_generic)
            }
        }
    }

    private fun loginSuccessful(displayName: String? = null) {
        startActivity(MainActivity.intentBuilder(this, displayName))
        finish()
    }

    private fun loginFailed(errorMessage: Int) {
        toast(errorMessage)
        finish()
    }
}