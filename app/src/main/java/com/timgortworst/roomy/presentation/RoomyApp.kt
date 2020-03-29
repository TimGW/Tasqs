package com.timgortworst.roomy.presentation

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.jakewharton.threetenabp.AndroidThreeTen
import com.timgortworst.roomy.BuildConfig
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.data.di.*
import com.timgortworst.roomy.domain.usecase.ForceUpdateUseCase
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import java.util.*

/**
 * Created by tim.gortworst on 17/02/2018.
 */
class RoomyApp : Application(), Configuration.Provider {
    private val sharedPref: SharedPrefs by inject()
    private val appComponent: List<Module> = listOf(
        appModule,
        repositoryModule,
        useCaseModule,
        presenterModule,
        viewModelModule
    )

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) androidLogger(Level.DEBUG)
            androidContext(this@RoomyApp)
            modules(appComponent)
        }

        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        setupFirebaseRemoteConfig()

        val nightMode = when (sharedPref.getDarkModeSetting()) {
            0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            1 -> AppCompatDelegate.MODE_NIGHT_NO
            2 -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)

        MobileAds.initialize(this, getString(R.string.ad_app_id))
    }

    private fun setupFirebaseRemoteConfig() {
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        val defaults = HashMap<String, Any>()
        defaults[ForceUpdateUseCase.KEY_CURRENT_REQUIRED_VERSION] = BuildConfig.VERSION_NAME
        defaults[ForceUpdateUseCase.KEY_CURRENT_RECOMMENDED_VERSION] = BuildConfig.VERSION_NAME
        defaults[KEY_ENABLE_ADS] = true
        defaults[ForceUpdateUseCase.KEY_UPDATE_URL] = "market://details?id=com.timgortworst.roomy"
        firebaseRemoteConfig.setDefaultsAsync(defaults)

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setFetchTimeoutInSeconds(0)
            .setDeveloperModeEnabled(true)
            .build()

        firebaseRemoteConfig.setConfigSettingsAsync(configSettings).addOnSuccessListener {
            firebaseRemoteConfig.fetchAndActivate()
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val builder = Configuration.Builder()
        if (BuildConfig.DEBUG) {
            builder.setMinimumLoggingLevel(Log.DEBUG)
        } else {
            builder.setMinimumLoggingLevel(Log.INFO)
        }
        return builder.build()
    }

    companion object {
        const val TAG = "RoomyApp"
        const val KEY_ENABLE_ADS = "enable_ads"
        private lateinit var instance: RoomyApp

        fun getAppVersion(): String {
            var result = ""
            try {
                result = instance.packageManager
                    .getPackageInfo(instance.packageName, 0)
                    .versionName
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, e.message.toString())
            }
            return result
        }
    }
}
