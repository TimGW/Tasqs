package com.timgortworst.roomy.presentation.base

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.threetenabp.AndroidThreeTen
import com.timgortworst.roomy.BuildConfig
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.data.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject


/**
 * Created by tim.gortworst on 17/02/2018.
 */
class RoomyApp : Application(), HasActivityInjector, Configuration.Provider {
    @Inject
    internal lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var sharedPref: SharedPrefs

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
            .context(this)
            .build()
            .inject(this)

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

    override fun activityInjector(): AndroidInjector<Activity>? {
        return activityInjector
    }

    companion object {
        private lateinit var instance: RoomyApp

        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }
}
