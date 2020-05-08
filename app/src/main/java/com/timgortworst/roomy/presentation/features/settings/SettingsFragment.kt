package com.timgortworst.roomy.presentation.features.settings

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.presentation.base.snackbar
import com.timgortworst.roomy.presentation.features.splash.SplashActivity
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var parentActivity: SettingsActivity
    private val settingsViewModel: SettingsViewModel by viewModel()
    private var counter: Int = 0
    private var snackbar: Snackbar? = null
    private var remainingTimeCounter: CountDownTimer? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = (activity as? SettingsActivity) ?: return
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    @SuppressLint("ShowToast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountPrefs()
        displayPrefs()
        privacyPrefs()
        aboutPrefs()

        settingsViewModel.easterEgg.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Response.Success -> { response.data?.let { easterEggMsg(it.id, it.data) } }
            }
        })
    }

    private fun accountPrefs() {
        val userNamePref = (findPreference("preferences_account_name_key") as? Preference)
        settingsViewModel.currentUser.observe(viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Response.Success -> response.data?.name?.let { userNamePref?.summary = it }
                    is Response.Error -> userNamePref?.summary = getString(R.string.error_generic)
                }
            })

        (findPreference("preferences_account_logout_key") as? Preference)
            ?.setOnPreferenceClickListener {
                AlertDialog.Builder(parentActivity)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.confirm_logout_message))
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        signOut()
                    }
                    .setNegativeButton(android.R.string.no) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

                true
            }
    }

    private fun displayPrefs() {
        val darkModePref = (findPreference("dark_mode_key") as? ListPreference)

        darkModePref?.summary = resources
            .getStringArray(R.array.night_mode_items)[settingsViewModel.getDarkModeSetting()]

        darkModePref?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                val darkModeSetting =
                    (newValue as String).toIntOrNull() ?: return@OnPreferenceChangeListener false
                val nightMode = when (darkModeSetting) {
                    0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    1 -> AppCompatDelegate.MODE_NIGHT_NO
                    2 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
                }
                AppCompatDelegate.setDefaultNightMode(nightMode)
                settingsViewModel.setDarkModeSetting(darkModeSetting)
                darkModePref?.summary =
                    resources.getStringArray(R.array.night_mode_items)[darkModeSetting]
                true
            }

    }

    private fun privacyPrefs() {
        (findPreference("analytics_key") as? SwitchPreferenceCompat)?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                val isAnalyticsEnabled = newValue as Boolean
                FirebaseAnalytics.getInstance(parentActivity)
                    .setAnalyticsCollectionEnabled(isAnalyticsEnabled)
                true
            }

        (findPreference("privacy_policy_key") as? Preference)?.setOnPreferenceClickListener {
            startActivity(
                HtmlTextActivity.intentBuilder(
                    parentActivity,
                    getString(R.string.privacy_policy),
                    getString(R.string.privacy_policy_title)
                )
            )
            true
        }
    }

    private fun aboutPrefs() {
        (findPreference("preferences_rate_app_key") as? Preference)?.setOnPreferenceClickListener {
            val intent = try {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${parentActivity.packageName}")
                )
            } catch (ex: ActivityNotFoundException) {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${parentActivity.packageName}")
                )
            }
            startActivity(intent)
            true
        }

        (findPreference("preferences_app_version_key") as? Preference)?.setOnPreferenceClickListener {
            settingsViewModel.onAppVersionClick(++counter)

            remainingTimeCounter?.cancel()
            remainingTimeCounter?.start()

            true
        }

        remainingTimeCounter = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                counter = 0
            }
        }
    }

    private fun signOut() = settingsViewModel.viewModelScope.launch {
        AuthUI.getInstance().signOut(parentActivity)
            .addOnSuccessListener {
                context?.cacheDir?.deleteRecursively() // clear cache
                parentActivity.finishAffinity()
                startActivity(SplashActivity.intentBuilder(parentActivity))
            }
            .addOnFailureListener {
                errorMessage()
            }
    }

    private fun easterEggMsg(stringRes: Int, argument: Int?) {
        val rootView = activity?.findViewById<View>(android.R.id.content) ?: return
        val snackText = getString(stringRes, argument)

        if (snackbar == null) {
            snackbar = Snackbar.make(rootView, snackText, Snackbar.LENGTH_LONG)
        }
        snackbar?.setText(snackText)
        snackbar?.show()
    }

    private fun errorMessage() {
        val rootView = activity?.findViewById<View>(android.R.id.content) ?: return
        rootView.snackbar(
            message = getString(R.string.delete_account_fail_msg),
            actionMessage = getString(R.string.retry)
        ) {
            signOut()
        }
    }
}