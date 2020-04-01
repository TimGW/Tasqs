package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { TaskRepository(get(), get()) }
    single { HouseholdRepository(get()) }
    single { UserRepository(get()) }
}