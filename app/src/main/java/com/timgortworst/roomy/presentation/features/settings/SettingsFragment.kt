package com.timgortworst.roomy.presentation.features.settings

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
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
import com.google.firebase.auth.GoogleAuthProvider
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.domain.utils.snackbar
import com.timgortworst.roomy.presentation.features.splash.SplashActivity
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsFragment : PreferenceFragmentCompat(), SettingsView {
    private lateinit var parentActivity: SettingsActivity

    private val sharedPref: SharedPrefs by inject()
    private val settingsViewModel: SettingsViewModel by viewModel()
    private val presenter: SettingsPresenter by inject { parametersOf(this) }

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
    }

    private fun accountPrefs() {
        val userNamePref = (findPreference("preferences_account_name_key") as? Preference)
        settingsViewModel.fetchUser().observe(viewLifecycleOwner, Observer { user ->
            user?.name?.let { userNamePref?.summary = it }
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

        (findPreference("preferences_account_delete_key") as? Preference)
            ?.setOnPreferenceClickListener {
                AlertDialog.Builder(parentActivity)
                    .setTitle(getString(R.string.delete))
                    .setMessage(getString(R.string.delete_account_dialog_text))
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        deleteAccount()
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
        darkModePref?.summary =
            resources.getStringArray(R.array.night_mode_items)[sharedPref.getDarkModeSetting()]
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
                sharedPref.setDarkModeSetting(darkModeSetting)
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
            HtmlTextActivity.start(
                parentActivity,
                getString(R.string.privacy_policy),
                getString(R.string.privacy_policy_title)
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
            presenter.onAppVersionClick(++counter)

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

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(parentActivity)
            .addOnCompleteListener {
                context?.cacheDir?.deleteRecursively() // clear cache

                SplashActivity.start(parentActivity)
            }
    }

    private fun deleteAccount() { // todo re-authenticatie before removal
        settingsViewModel.viewModelScope.launch {
            settingsViewModel.deleteFirestoreData()

            AuthUI.getInstance()
                .delete(parentActivity)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        context?.cacheDir?.deleteRecursively() // clear cache
                        (context?.getSystemService(ACTIVITY_SERVICE) as? ActivityManager)
                            ?.clearApplicationUserData() // clear app data
                    } else {
                        val rootView = activity?.findViewById<View>(android.R.id.content)
                            ?: return@addOnCompleteListener
                        rootView.snackbar(
                            message = getString(R.string.delete_account_fail_msg),
                            actionMessage = getString(R.string.retry)
                        ) {
                            deleteAccount()
                        }
                    }
                }
        }
    }

    override fun easterEggMsg(stringRes: Int, argument: Int?) {
        val rootView = activity?.findViewById<View>(android.R.id.content) ?: return
        val snackText = getString(stringRes, argument)

        if (snackbar == null) {
            snackbar = Snackbar.make(rootView, snackText, Snackbar.LENGTH_LONG)
        }
        snackbar?.setText(snackText)
        snackbar?.show()
    }
}