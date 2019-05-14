package com.timgortworst.roomy.ui.agenda.module

import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.agenda.presenter.EditAgendaEventPresenter
import com.timgortworst.roomy.ui.agenda.ui.EditAgendaEventActivity
import com.timgortworst.roomy.ui.agenda.ui.EditAgendaEventView
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class EditAgendaEventModule {

    @Binds
    internal abstract fun provideEditAgendaEventView(editAgendaEventActivity: EditAgendaEventActivity): EditAgendaEventView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideEditAgendaEventPresenter(
            view: EditAgendaEventView,
            agendaRepository: AgendaRepository,
            userRepository: UserRepository
        ): EditAgendaEventPresenter {
            return EditAgendaEventPresenter(view, agendaRepository, userRepository)
        }
    }
}
