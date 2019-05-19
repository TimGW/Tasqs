package com.timgortworst.roomy.ui.splash.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.roomy.R
import com.timgortworst.roomy.ui.googlesignin.view.GoogleSignInActivity
import com.timgortworst.roomy.ui.setup.view.SetupActivity
import com.timgortworst.roomy.ui.splash.presenter.SplashPresenter
import com.timgortworst.roomy.utils.Constants
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : AppCompatActivity(), SplashView {

    @Inject
    lateinit var presenter: SplashPresenter

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SetupActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setTheme(R.style.AppTheme_Launcher)
        super.onCreate(savedInstanceState)

        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnCompleteListener {
            val referredHouseholdId = it.result?.link?.getQueryParameter(Constants.QUERY_PARAM_HOUSEHOLD).orEmpty()
            presenter.userLogin(referredHouseholdId)
        }
    }

    override fun goToGoogleSignInActivity() {
        GoogleSignInActivity.start(this)
        finish()
    }

    override fun goToSetupActivity(referredHouseholdId: String) {
        if (referredHouseholdId.isNotBlank()) {
            SetupActivity.start(this, referredHouseholdId)
        } else {
            SetupActivity.start(this)
        }
        finish()
    }

    override fun userInvalid() {
        finish()
    }
}
