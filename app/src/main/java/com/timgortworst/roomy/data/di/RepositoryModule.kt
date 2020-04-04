package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.data.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    single { TaskRepository(get(), get()) }
    single<HouseholdRepository> { HouseholdRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
}