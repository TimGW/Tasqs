package com.timgortworst.roomy.ui.agenda.module

import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.agenda.presenter.AgendaPresenter
import com.timgortworst.roomy.ui.agenda.ui.AgendaFragment
import com.timgortworst.roomy.ui.agenda.ui.AgendaView
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class AgendaModule {

    @Binds
    internal abstract fun provideMainAgendaView(mainAgendaFragment: AgendaFragment): AgendaView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideMainAgendaPresenter(
            mainAgendaFragmentView: AgendaView,
            agendaRepository: AgendaRepository
        ): AgendaPresenter {
            return AgendaPresenter(mainAgendaFragmentView, agendaRepository)
        }
    }
}
