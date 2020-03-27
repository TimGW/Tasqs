package com.timgortworst.roomy.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { FirebaseFirestore.getInstance() }
    single { SharedPrefs(androidContext()) }

    single { TaskRepository(get(), get()) }
    single { HouseholdRepository(get()) }
    single { UserRepository(get()) }
}
