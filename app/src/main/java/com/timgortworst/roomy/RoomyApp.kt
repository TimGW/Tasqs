package com.timgortworst.roomy

import android.app.Activity
import android.app.Application
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.di.DaggerAppComponent
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
