package com.timgortworst.roomy.ui.agenda.module

import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.agenda.presenter.AgendaPresenter
import com.timgortworst.roomy.ui.agenda.view.AgendaFragment
import com.timgortworst.roomy.ui.agenda.view.AgendaView
import dagger.Binds
import dagger.Module
import dagger.Provides

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
            agendaRepository: AgendaRepository,
            userRepository: UserRepository
        ): AgendaPresenter {
            return AgendaPresenter(mainAgendaFragmentView, agendaRepository, userRepository)
        }
    }
}
