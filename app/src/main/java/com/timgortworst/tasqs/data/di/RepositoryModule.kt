package com.timgortworst.tasqs.data.di

import com.timgortworst.tasqs.data.mapper.*
import com.timgortworst.tasqs.data.repository.HouseholdRepositoryImpl
import com.timgortworst.tasqs.data.repository.TaskRepositoryImpl
import com.timgortworst.tasqs.data.repository.UserRepositoryImpl
import com.timgortworst.tasqs.domain.repository.HouseholdRepository
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TaskRepository> {
        val taskDataMapper = TaskDataMapper()
        TaskRepositoryImpl(get(), taskDataMapper, ListMapperImpl(taskDataMapper))
    }
    single<HouseholdRepository> { HouseholdRepositoryImpl(HouseholdDataMapper()) }
    single<UserRepository> {
        val taskDataMapper = UserDataMapper()
        UserRepositoryImpl(taskDataMapper, NullableOutputListMapperImpl(taskDataMapper))
    }
}