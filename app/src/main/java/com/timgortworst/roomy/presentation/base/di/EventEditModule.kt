package com.timgortworst.roomy.presentation.base.di

import com.timgortworst.roomy.presentation.features.event.view.EventEditActivity
import com.timgortworst.roomy.presentation.features.event.view.EventEditView
import dagger.Binds
import dagger.Module

@Module
abstract class EventEditModule {
    @Binds
    internal abstract fun provideEventEditView(editAgendaEventActivity: EventEditActivity): EventEditView
}
