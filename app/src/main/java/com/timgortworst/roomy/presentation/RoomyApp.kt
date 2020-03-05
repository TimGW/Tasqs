package com.timgortworst.roomy.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.threetenabp.AndroidThreeTen
import com.timgortworst.roomy.BuildConfig
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.data.di.presenterModule
import com.timgortworst.roomy.data.di.repositoryModule
import com.timgortworst.roomy.data.di.useCaseModule
import com.timgortworst.roomy.data.di.viewModelModule
import com.timgortworst.roomy.domain.utils.timeCalcModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * Created by tim.gortworst on 17/02/2018.
 */
class RoomyApp : Application(), Configuration.Provider {
    private val sharedPref: SharedPrefs by inject()
    private val appComponent: List<Module> = listOf(
            repositoryModule,
            useCaseModule,
            presenterModule,
            timeCalcModule,
            viewModelModule)

    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@RoomyApp)
            modules(appComponent)
        }

        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        val nightMode = when(sharedPref.getDarkModeSetting()) {
            0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            1 -> AppCompatDelegate.MODE_NIGHT_NO
            2 -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)

        MobileAds.initialize(this, getString(R.string.ad_app_id))
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val builder = Configuration.Builder()
         if (BuildConfig.DEBUG) {
             builder.setMinimumLoggingLevel(android.util.Log.DEBUG)
         } else {
             builder.setMinimumLoggingLevel(android.util.Log.INFO)
        }
        return builder.build()
    }

    companion object {
        const val TAG = "RoomyApp"
    }
}
