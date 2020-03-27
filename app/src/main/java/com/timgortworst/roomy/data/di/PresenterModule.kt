package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.presentation.features.task.presenter.TaskEditPresenter
import com.timgortworst.roomy.presentation.features.task.view.TaskEditView
import org.koin.dsl.module

val presenterModule = module {
    factory { (view: TaskEditView) -> TaskEditPresenter(view, get(),get()) }
}
