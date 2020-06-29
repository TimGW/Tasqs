package com.timgortworst.tasqs.data.di

import com.timgortworst.tasqs.data.sharedpref.SharedPrefManager
import com.timgortworst.tasqs.data.sharedpref.SharedPrefs
import com.timgortworst.tasqs.presentation.features.notifications.NotificationWorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    factory { NotificationWorkManager(androidContext()) }
    single { SharedPrefManager(androidContext()) }
    single { SharedPrefs(get()) }
}