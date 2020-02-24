package com.timgortworst.roomy.data.di


import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { EventRepository() }
    single { HouseholdRepository() }
    single { UserRepository() }
}
