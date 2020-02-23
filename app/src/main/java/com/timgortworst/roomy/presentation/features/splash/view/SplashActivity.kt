package com.timgortworst.roomy.presentation.features.splash.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder.Companion.QUERY_PARAM_HOUSEHOLD
import com.timgortworst.roomy.domain.utils.showToast
import com.timgortworst.roomy.presentation.features.main.view.MainActivity
import com.timgortworst.roomy.presentation.features.onboarding.view.OnboardingActivity
import com.timgortworst.roomy.presentation.features.splash.presenter.SplashPresenter
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SplashActivity : AppCompatActivity(), SplashView {
    private val presenter: SplashPresenter by inject { parametersOf(this) }
    private lateinit var referredHouseholdId: String

    companion object {
        private const val TAG = "SplashActivity"

        fun start(context: Context) {
            val intent = Intent(context, SplashActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MyTheme_NoActionBar_Launcher)
        super.onCreate(savedInstanceState)

        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener {
            val referredHouseholdId = it?.link?.getQueryParameter(QUERY_PARAM_HOUSEHOLD).orEmpty()
            presenter.initializeUser(referredHouseholdId)
        }.addOnFailureListener {
            Log.e(TAG, it.localizedMessage.orEmpty())
            showToast(R.string.error_generic)
            finish()
        }
    }

    override fun goToOnboardingActivity() {
        OnboardingActivity.start(this)
        finish()
    }

    override fun goToMainActivity() {
        MainActivity.start(this)
        finish()
    }

    override fun presentHouseholdOverwriteDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_household_overwrite_title))
                .setMessage(getString(R.string.dialog_household_overwrite_text))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    presenter.changeCurrentUserHousehold(referredHouseholdId)
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    goToMainActivity()
                }
                .show()
    }

    override fun presentAlreadyInHouseholdDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_household_similar_title))
                .setMessage(getString(R.string.dialog_household_similar_text))
                .setNeutralButton(android.R.string.yes) { _, _ ->
                    goToMainActivity()
                }
                .show()
    }

//    override fun presentUserIsBannedDialog() {
//        AlertDialog.Builder(this)
//                .setTitle(getString(R.string.dialog_household_banned_title))
//                .setMessage(getString(R.string.dialog_household_banned_text))
//                .setPositiveButton(android.R.string.yes) { _, _ ->
//                    presenter.setupNewHousehold()
//                }
//                .setNegativeButton(android.R.string.no) { _, _ ->
//                    finish()
//                }
//                .show()
//    }
//
//    override fun presentHouseholdFullDialog() {
//        AlertDialog.Builder(this)
//                .setTitle(getString(R.string.dialog_household_full_title))
//                .setMessage(getString(R.string.dialog_household_full_text))
//                .setNeutralButton(android.R.string.ok) { _, _ ->
//                    finish()
//                }
//                .show()
//    }
}
