package com.timgortworst.roomy.presentation.features.onboarding.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.timgortworst.roomy.R
import com.timgortworst.roomy.presentation.features.onboarding.presenter.AuthenticationPresenter
import kotlinx.android.synthetic.main.fragment_onboarding.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class OnboardingAuthFragment : Fragment(), AuthCallback {
    private val presenter: AuthenticationPresenter by inject { parametersOf(this) }
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var parentActivity: OnboardingActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = (activity as? OnboardingActivity) ?: return
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)
        view.goolge_sign_in.visibility = View.VISIBLE
        view.onboarding_image.setImageResource(R.drawable.onboarding_connect)
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

    fun signInAnonymously() { presenter.signInAnonymously() }

//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                showProgressDialog()
//                val account = task.getResult(ApiException::class.java) as GoogleSignInAccount
//                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//                presenter.signInWithCredential(credential)
//            } catch (e: ApiException) {
//                setResult(Activity.RESULT_CANCELED, Intent())
//                finish()
//            }
//        }
//    }

    override fun loginSuccessful(user: FirebaseUser) {
//        setResult(Activity.RESULT_OK, Intent())
//        finish()
    }

    override fun loginFailed() {
//        setResult(Activity.RESULT_CANCELED, Intent())
//        finish()
    }

    companion object {
        private const val RC_SIGN_IN = 9001

        fun newInstance(): OnboardingAuthFragment {
            return OnboardingAuthFragment()
        }
    }
}