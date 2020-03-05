package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.IdProvider
import com.timgortworst.roomy.data.repository.UserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { IdProvider(get()) }
    single { TaskRepository(get()) }
    single { HouseholdRepository() }
    single { UserRepository(get()) }
    single { SharedPrefs(androidContext()) }
}
