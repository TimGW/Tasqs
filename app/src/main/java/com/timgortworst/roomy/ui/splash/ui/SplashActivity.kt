package com.timgortworst.roomy.ui.splash.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.roomy.R
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.ui.setup.view.SetupActivity
import com.timgortworst.roomy.ui.signin.view.SignInActivity
import com.timgortworst.roomy.ui.splash.presenter.SplashPresenter
import com.timgortworst.roomy.utils.Constants
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : AppCompatActivity(), SplashView {

    @Inject
    lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setTheme(R.style.AppTheme_Launcher)
        super.onCreate(savedInstanceState)

        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnCompleteListener { it ->
            val householdId = it.result?.link?.getQueryParameter(Constants.QUERY_PARAM_HOUSEHOLD).orEmpty()
            presenter.initializeApplication(householdId)
        }
    }

    override fun userNotLoggedIn() {
        SignInActivity.start(this)
        finish()
    }

    override fun userSetupValid() {
        MainActivity.start(this)
        finish()
    }

    override fun userSetupInvalid() {
        SetupActivity.start(this)
        finish()
    }

    override fun userAcceptedInvite(householdId: String) {
        SetupActivity.start(this, householdId)
    }
}
