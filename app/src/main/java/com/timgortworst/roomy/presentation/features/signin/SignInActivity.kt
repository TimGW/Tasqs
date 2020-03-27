package com.timgortworst.roomy.presentation.features.signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.timgortworst.roomy.BuildConfig
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.ResponseState
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.utils.toast
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.main.MainActivity
import com.timgortworst.roomy.presentation.features.task.view.TaskInfoActivity
import org.koin.android.viewmodel.ext.android.viewModel

class SignInActivity : BaseActivity() {
    private val viewModel : SignInViewModel by viewModel()

    companion object {
        private const val RC_SIGN_IN = 123

        fun intentBuilder(context: Context): Intent {
            return Intent(context, SignInActivity::class.java)
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
                viewModel.handleLoginResult(response).observe(this@SignInActivity, Observer {
                    it.getContentIfNotHandled()?.let { event -> handResponse(event, response) }
                })
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

    private fun loginSuccessful() {
        startActivity(MainActivity.intentBuilder(this))
        finish()
    }

    private fun welcomeBack(displayName: String?) {
        startActivity(MainActivity.intentBuilder(this, displayName))
        finish()
    }

    private fun loginFailed(errorMessage: Int) {
        toast(errorMessage)
        finishAffinity()
    }

    private fun handResponse(response: ResponseState, idpResponse: IdpResponse) {
        when(response) {
            is ResponseState.Success<*> -> {
                if (idpResponse.isNewUser) {
                    loginSuccessful()
                } else {
                    welcomeBack(response.data as? String)
                }
            }
            is ResponseState.Error -> loginFailed(response.message)
        }
    }
}