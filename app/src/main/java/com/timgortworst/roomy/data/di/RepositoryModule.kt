package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.data.repository.*
import com.timgortworst.roomy.domain.repository.HouseholdRepository
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TaskRepository> { TaskRepositoryImpl(get(), get()) }
    single<HouseholdRepository> { HouseholdRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}