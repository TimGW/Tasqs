package com.timgortworst.roomy.ui.agenda.module

import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.agenda.presenter.EditAgendaEventPresenter
import com.timgortworst.roomy.ui.agenda.view.EditAgendaEventActivity
import com.timgortworst.roomy.ui.agenda.view.EditAgendaEventView
import dagger.Binds
import dagger.Module
import dagger.Provides

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
            userRepository: UserRepository,
            sharedPref: HuishoudGenootSharedPref
        ): EditAgendaEventPresenter {
            return EditAgendaEventPresenter(view, agendaRepository, userRepository, sharedPref)
        }
    }
}
