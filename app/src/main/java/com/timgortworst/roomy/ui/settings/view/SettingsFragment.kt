package com.timgortworst.roomy.ui.settings.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.R
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.ui.settings.presenter.SettingsPresenter
import com.timgortworst.roomy.utils.showToast
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), SettingsView {

    @Inject
    lateinit var settingsPresenter: SettingsPresenter

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

        val darkModeSwitch: SwitchPreferenceCompat? = findPreference("dark_mode_key")

        darkModeSwitch?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            val isDarkModeOn = newValue as Boolean

            AppCompatDelegate.setDefaultNightMode (
                    if (isDarkModeOn) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
            )
            sharedPref.setDisplayModeDark(isDarkModeOn)
            activity?.recreate()
            true
        }

        val logoutButton: Preference? = findPreference("logout_key")
        logoutButton?.setOnPreferenceClickListener {
            AlertDialog.Builder(activity!!)
                    .setTitle(getString(R.string.dialog_household_overwrite_title))
                    .setMessage(getString(R.string.dialog_household_overwrite_text))
                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                        settingsPresenter.deleteUser(FirebaseAuth.getInstance().currentUser!!.uid)
                    }
                    .setNegativeButton(android.R.string.no) { dialog, which ->
                        activity!!.showToast("no")
                    }
                    .show()
            true
        }
    }
}