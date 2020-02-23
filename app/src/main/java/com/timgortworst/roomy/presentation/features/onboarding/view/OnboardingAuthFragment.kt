package com.timgortworst.roomy.presentation.features.onboarding.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.utils.showToast
import com.timgortworst.roomy.presentation.features.onboarding.presenter.OnboardingPresenter
import kotlinx.android.synthetic.main.fragment_onboarding.view.onboarding_headline
import kotlinx.android.synthetic.main.fragment_onboarding.view.onboarding_subtitle
import kotlinx.android.synthetic.main.fragment_onboarding_auth.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class OnboardingAuthFragment : Fragment(), AuthCallback {
    private val presenter: OnboardingPresenter by inject { parametersOf(this) }
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var parentActivity: OnboardingActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = (activity as? OnboardingActivity) ?: return
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_auth, container, false)
        view.onboarding_headline.setText(R.string.onboarding_title_login)
        view.onboarding_subtitle.setText(R.string.onboarding_subtitle_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(parentActivity, gso)

        view.goolge_sign_in.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }

        return view
    }

    fun signInAnonymously() {
        presenter.signInAnonymously()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

            presenter.signInWithCredential(credential)
        }
    }

    override fun setupSuccessful() {
        parentActivity.goToMain()
    }

    override fun setupFailed() {
        parentActivity.showToast(R.string.error_generic)
        parentActivity.finish()
    }

    companion object {
        private const val RC_SIGN_IN = 9001

        fun newInstance(): OnboardingAuthFragment {
            return OnboardingAuthFragment()
        }
    }
}