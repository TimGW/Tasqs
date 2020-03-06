package com.timgortworst.roomy.presentation.features.auth

import android.app.Activity
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
import com.timgortworst.roomy.databinding.FragmentGoogleAuthBinding
import com.timgortworst.roomy.domain.utils.showToast
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.main.MainActivity
import com.timgortworst.roomy.presentation.features.onboarding.OnboardingPresenter
import com.timgortworst.roomy.presentation.features.onboarding.OnboardingActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class AuthFragment : Fragment(), AuthCallback {
    private val presenter: OnboardingPresenter by inject { parametersOf(this) }
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var parentActivity: Activity

    private var _binding: FragmentGoogleAuthBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = (activity as? Activity) ?: return
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGoogleAuthBinding.inflate(inflater, container, false)

        binding.onboardingHeadline.setText(R.string.onboarding_title_login)
        binding.onboardingSubtitle.setText(R.string.onboarding_subtitle_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(parentActivity, gso)

        binding.goolgeSignIn.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }

        return binding.root
    }

    fun signInAnonymously() {
        (parentActivity as? BaseActivity)?.showProgressDialog()
        presenter.signInAnonymously()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

            (parentActivity as? BaseActivity)?.showProgressDialog()
            presenter.signInOrLinkCredential(credential, account?.displayName)
        }
    }

    override fun setupSuccessful() {
        (parentActivity as? BaseActivity)?.hideProgressDialog()
        when (parentActivity) {
            is OnboardingActivity -> (parentActivity as? OnboardingActivity)?.goToMain()
            is MainActivity -> (parentActivity as? MainActivity)?.presentUsersFragment()
        }
    }

    override fun setupFailed() {
        (parentActivity as? BaseActivity)?.hideProgressDialog()
        parentActivity.showToast(R.string.error_generic)
        parentActivity.finish()
    }

    companion object {
        private const val RC_SIGN_IN = 9001

        fun newInstance(): AuthFragment {
            return AuthFragment()
        }
    }
}