package com.timgortworst.roomy.utils

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
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
