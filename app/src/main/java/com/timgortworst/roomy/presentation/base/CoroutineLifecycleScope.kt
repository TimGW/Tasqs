package com.timgortworst.roomy.presentation.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.CoroutineContext

@Deprecated("use viewmodelscope")
class CoroutineLifecycleScope(private val dispatcher: CoroutineDispatcher) : CoroutineScope, LifecycleObserver {
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = job + dispatcher

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        coroutineContext.cancelChildren()
    }
}
