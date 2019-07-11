package com.timgortworst.roomy.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CoroutineLifecycleScope(private val dispatcher: CoroutineDispatcher) : CoroutineScope, LifecycleObserver {
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = job + dispatcher

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        coroutineContext.cancelChildren()
    }
}
