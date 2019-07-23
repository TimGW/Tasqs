package com.timgortworst.roomy

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.di.DaggerAppComponent
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject




/**
 * Created by tim.gortworst on 17/02/2018.
 */
class RoomyApp : Application(), HasActivityInjector {

    @Inject
    internal lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var sharedPref: HuishoudGenootSharedPref

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
            .context(this)
            .build()
            .inject(this)

        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        AppCompatDelegate.setDefaultNightMode(
            if (sharedPref.isDisplayModeDark())
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )

        MobileAds.initialize(this, getString(R.string.ad_app_id))
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
