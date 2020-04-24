package com.timgortworst.roomy.presentation.features.splash

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import com.timgortworst.roomy.domain.usecase.forcedupdate.ForceUpdateUseCaseImpl
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder.Companion.QUERY_PARAM_HOUSEHOLD
import com.timgortworst.roomy.domain.utils.snackbar
import com.timgortworst.roomy.presentation.RoomyApp
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.main.MainActivity
import com.timgortworst.roomy.presentation.features.signin.SignInActivity
import com.timgortworst.roomy.presentation.usecase.settings.ForceUpdateUseCase
import org.koin.android.ext.android.inject

class SplashActivity : BaseActivity(), ForceUpdateUseCase {
    private val viewModel: SplashViewModel by inject()

    companion object {
        fun intentBuilder(context: Context): Intent {
            return Intent(context, SplashActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MyTheme_NoActionBar_Launcher)
        super.onCreate(savedInstanceState)

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val currentVersion = RoomyApp.getAppVersion()

        ForceUpdateUseCaseImpl.with(remoteConfig)
            .onUpdateNeeded(this)
            .check(currentVersion)

        viewModel.startupAction.observe(this, Observer {
            when (it) {
                Response.Loading -> showProgressDialog()
                is Response.Error -> {
                    val rootView = findViewById<View>(android.R.id.content) ?: return@Observer
                    rootView.snackbar(message = getString(R.string.error_generic))
                }
                is Response.Success -> {
                    when (it.data) {
                        StartUpAction.TriggerSignInFlow -> goToSignInActivity()
                        StartUpAction.TriggerMainFlow -> goToMainActivity()
                        StartUpAction.DialogSameId -> presentAlreadyInHouseholdDialog()
                        is StartUpAction.DialogOverrideId -> presentHouseholdOverwriteDialog(it.data.id)
                    }
                }
            }
        })
    }

    private fun goToSignInActivity() {
        startActivity(SignInActivity.intentBuilder(this))
        finish()
    }

    private fun goToMainActivity() {
        startActivity(MainActivity.intentBuilder(this))
        finish()
    }

    private fun presentHouseholdOverwriteDialog(referredHouseholdId: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_household_overwrite_title))
            .setMessage(getString(R.string.dialog_household_overwrite_text))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                viewModel.switchHousehold(referredHouseholdId)
            }
            .setNegativeButton(android.R.string.no) { _, _ ->
                goToMainActivity()
            }
            .show()
    }

    private fun presentAlreadyInHouseholdDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_household_similar_title))
            .setMessage(getString(R.string.dialog_household_similar_text))
            .setNeutralButton(android.R.string.yes) { _, _ ->
                goToMainActivity()
            }
            .show()
    }

    override fun onUpdateNeeded(updateUrl: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.forced_update_dialog_title))
            .setMessage(getString(R.string.forced_update_dialog_text))
            .setPositiveButton(getString(R.string.forced_update_dialog_positive_button)) { _, _ ->
                redirectStore(updateUrl)
                finish()
            }
            .setNegativeButton(getString(R.string.forced_update_dialog_negative_button)) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    override fun onUpdateRecommended(updateUrl: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.recommended_update_dialog_title))
            .setMessage(getString(R.string.recommended_update_dialog_text))
            .setPositiveButton(getString(R.string.recommended_update_dialog_positive_button)) { _, _ ->
                redirectStore(updateUrl)
                finish()
            }
            .setNegativeButton(getString(R.string.recommended_update_dialog_negative_button)) { _, _ ->
                noUpdateNeeded()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    override fun noUpdateNeeded() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener {
            val referredHouseholdId = it?.link?.getQueryParameter(QUERY_PARAM_HOUSEHOLD).orEmpty()
            viewModel.handleAppStartup(referredHouseholdId)
        }.addOnFailureListener {
            val content = findViewById<View>(android.R.id.content)
            content.snackbar(getString(R.string.error_generic))
            finish()
        }
    }

    private fun redirectStore(updateUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
