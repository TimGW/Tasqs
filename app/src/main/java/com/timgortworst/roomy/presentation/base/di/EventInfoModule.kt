package com.timgortworst.roomy.presentation.base.di

import com.timgortworst.roomy.presentation.features.event.view.EventInfoActivity
import com.timgortworst.roomy.presentation.features.event.view.EventInfoView
import dagger.Binds
import dagger.Module

@Module
abstract class EventInfoModule {
    @Binds
    internal abstract fun provideEventInfoView(editEventInfoActivity: EventInfoActivity): EventInfoView
}
