package com.timgortworst.roomy.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.presentation.features.notifications.NotificationWorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    factory { NotificationWorkManager(androidContext()) }

    single { FirebaseFirestore.getInstance() }
    single { SharedPrefs(androidContext()) }
}