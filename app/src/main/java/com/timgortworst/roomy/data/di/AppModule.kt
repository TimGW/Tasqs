package com.timgortworst.roomy.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.timgortworst.roomy.data.sharedpref.SharedPrefManager
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.presentation.features.notifications.NotificationWorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    factory { NotificationWorkManager(androidContext()) }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseRemoteConfig.getInstance() }
    single { FirebaseAuth.getInstance() }
    single { SharedPrefManager(androidContext()) }
    single { SharedPrefs(get()) }
}