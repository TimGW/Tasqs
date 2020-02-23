package com.timgortworst.roomy.presentation.features.event.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.event.view.EventInfoView
import kotlinx.coroutines.Dispatchers
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.*

class EventInfoPresenter(
        private val view: EventInfoView
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun formatDate(zonedDateTime: ZonedDateTime) {
        val formattedDayOfMonth = zonedDateTime.dayOfMonth.toString()
        val formattedMonth = zonedDateTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val formattedYear = zonedDateTime.year.toString()
        view.presentFormattedDate(formattedDayOfMonth, formattedMonth, formattedYear)
    }


    companion object {
        private const val TAG = "EventInfoPresenter"
    }
}
