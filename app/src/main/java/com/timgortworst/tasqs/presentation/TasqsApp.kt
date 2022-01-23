package com.timgortworst.tasqs.presentation

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
import com.timgortworst.tasqs.BuildConfig
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.data.sharedpref.SharedPrefs
import com.timgortworst.tasqs.data.di.*
import com.timgortworst.tasqs.domain.usecase.splash.ForcedUpdateUseCaseImpl
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import java.util.*

/**
 * Created by tim.gortworst on 17/02/2018
 */
class TasqsApp : Application(), Configuration.Provider {
    private val sharedPref: SharedPrefs by inject()
    private val appComponent: List<Module> = listOf(
        appModule,
        repositoryModule,
        useCaseModule,
        viewModelModule
    )

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) androidLogger(Level.DEBUG)
            androidContext(this@TasqsApp)
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
        defaults[ForcedUpdateUseCaseImpl.KEY_CURRENT_REQUIRED_VERSION] = BuildConfig.VERSION_NAME
        defaults[ForcedUpdateUseCaseImpl.KEY_CURRENT_RECOMMENDED_VERSION] = BuildConfig.VERSION_NAME
        defaults[KEY_ENABLE_ADS] = true
        defaults[ForcedUpdateUseCaseImpl.KEY_UPDATE_URL] = "market://details?id=com.timgortworst.tasqs"
        firebaseRemoteConfig.setDefaultsAsync(defaults)

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .setFetchTimeoutInSeconds(60)
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
        const val TAG = "TasqsApp"
        const val KEY_ENABLE_ADS = "enable_ads"
        const val LOADING_DELAY = 500L
        private lateinit var instance: TasqsApp

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
