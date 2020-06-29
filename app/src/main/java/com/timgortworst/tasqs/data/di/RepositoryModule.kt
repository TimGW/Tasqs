package com.timgortworst.tasqs.data.di

import com.timgortworst.tasqs.data.repository.*
import com.timgortworst.tasqs.domain.repository.HouseholdRepository
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<HouseholdRepository> { HouseholdRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl() }
}