package com.timgortworst.roomy.ui.event.module

import com.timgortworst.roomy.ui.event.view.EventEditActivity
import com.timgortworst.roomy.ui.event.view.EventEditView
import dagger.Binds
import dagger.Module

@Module
abstract class EventEditModule {
    @Binds
    internal abstract fun provideEventEditView(editAgendaEventActivity: EventEditActivity): EventEditView
}
