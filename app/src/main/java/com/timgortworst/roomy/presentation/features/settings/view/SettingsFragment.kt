package com.timgortworst.roomy.presentation.features.settings.view

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.HuishoudGenootSharedPref
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var sharedPref: HuishoudGenootSharedPref

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return

        (findPreference("dark_mode_key") as? SwitchPreferenceCompat)?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val isDarkModeOn = newValue as Boolean

                    AppCompatDelegate.setDefaultNightMode(
                            if (isDarkModeOn) {
                                AppCompatDelegate.MODE_NIGHT_YES
                            } else {
                                AppCompatDelegate.MODE_NIGHT_NO
                            }
                    )
                    sharedPref.setDisplayModeDark(isDarkModeOn)
                    activity.recreate()
                    true
                }


        (findPreference("analytics_key") as? SwitchPreferenceCompat)?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val isAnalyticsEnabled = newValue as Boolean
                    FirebaseAnalytics.getInstance(activity).setAnalyticsCollectionEnabled(isAnalyticsEnabled)
                    true
                }

        (findPreference("privacy_policy_key") as? Preference)?.setOnPreferenceClickListener {
            HtmlTextActivity.start(activity, getString(R.string.privacy_policy), "Privacy policy")
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
    }
}