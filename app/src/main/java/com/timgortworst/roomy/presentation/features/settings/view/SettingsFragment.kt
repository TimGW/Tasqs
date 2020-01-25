package com.timgortworst.roomy.presentation.features.settings.view

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.presentation.features.settings.presenter.SettingsPresenter
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), SettingsView {
    @Inject lateinit var sharedPref: SharedPrefs
    @Inject lateinit var presenter: SettingsPresenter
    private var toast: Toast? = null
    private var remainingTimeCounter: CountDownTimer? = null
    private var counter: Int = 0

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    @SuppressLint("ShowToast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return

        val darkModePref = (findPreference("dark_mode_key") as? ListPreference)
        darkModePref?.summary = resources.getStringArray(R.array.night_mode_items)[sharedPref.getDarkModeSetting()]
        darkModePref?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val darkModeSetting = (newValue as String).toIntOrNull() ?: return@OnPreferenceChangeListener false
                    val nightMode = when(darkModeSetting) {
                        0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        1 -> AppCompatDelegate.MODE_NIGHT_NO
                        2 -> AppCompatDelegate.MODE_NIGHT_YES
                        else -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
                    }
                    AppCompatDelegate.setDefaultNightMode(nightMode)
                    sharedPref.setDarkModeSetting(darkModeSetting)
                    darkModePref?.summary = resources.getStringArray(R.array.night_mode_items)[darkModeSetting]
                    true
                }

        (findPreference("analytics_key") as? SwitchPreferenceCompat)?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val isAnalyticsEnabled = newValue as Boolean
                    FirebaseAnalytics.getInstance(activity).setAnalyticsCollectionEnabled(isAnalyticsEnabled)
                    true
                }

        (findPreference("privacy_policy_key") as? Preference)?.setOnPreferenceClickListener {
            HtmlTextActivity.start(activity, getString(com.timgortworst.roomy.R.string.privacy_policy), "Privacy policy")
            true
        }

        (findPreference("preferences_rate_app_key") as? Preference)?.setOnPreferenceClickListener {
            val intent = try {
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${activity.packageName}"))
            } catch (ex: ActivityNotFoundException) {
                Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${activity.packageName}"))
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
            override fun onFinish() { counter = 0 }
        }
    }

    @SuppressLint("ShowToast")
    override fun toasti(stringRes: Int, argument: Int?) {
        val string = getString(stringRes, argument)

        if (toast == null) {
            toast = Toast.makeText(activity, string, Toast.LENGTH_LONG)
        }
        toast?.setText(string)
        toast?.show()
    }
}