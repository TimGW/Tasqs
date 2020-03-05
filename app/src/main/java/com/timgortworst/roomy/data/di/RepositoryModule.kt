package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.IdProvider
import com.timgortworst.roomy.data.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { IdProvider(get()) }
    single { EventRepository(get()) }
    single { HouseholdRepository() }
    single { UserRepository(get()) }
}
