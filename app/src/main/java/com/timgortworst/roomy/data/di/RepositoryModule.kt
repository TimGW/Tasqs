package com.timgortworst.roomy.data.di


import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.presentation.features.event.view.EventListFragment
import com.timgortworst.roomy.presentation.features.user.view.UserListFragment
import org.koin.dsl.module

val repositoryModule = module {
    single { EventRepository() }
    single { HouseholdRepository() }
    single { UserRepository() }
}
