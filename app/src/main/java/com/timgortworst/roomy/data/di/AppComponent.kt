package com.timgortworst.roomy.data.di

import android.content.Context
import com.timgortworst.roomy.presentation.base.RoomyApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * Main Dagger AppComponent which is used in @see [RoomyApp]
 */
@Singleton
@Component(
    modules = [(AppModule::class),
        (ActivityBuilder::class),
        (AndroidInjectionModule::class)]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(application: RoomyApp)
}

