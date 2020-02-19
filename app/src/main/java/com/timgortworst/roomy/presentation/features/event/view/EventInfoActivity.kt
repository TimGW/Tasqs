package com.timgortworst.roomy.presentation.features.event.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventRecurrence
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.event.presenter.EventInfoPresenter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_info_event.*
import javax.inject.Inject


class EventInfoActivity : BaseActivity(), EventInfoView {
    @Inject
    lateinit var presenter: EventInfoPresenter

    private lateinit var event: Event

    companion object {
        const val INTENT_EXTRA_INFO_EVENT = "INTENT_EXTRA_INFO_EVENT"

        fun start(context: AppCompatActivity, event: Event) {
            val intent = Intent(context, EventInfoActivity::class.java)
            intent.putExtra(INTENT_EXTRA_INFO_EVENT, event)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_event)

        if (intent.hasExtra(INTENT_EXTRA_INFO_EVENT)) {
            event = intent.getParcelableExtra(INTENT_EXTRA_INFO_EVENT) as Event
        } else {
            finish()
        }

        supportActionBar?.apply {
            title = getString(R.string.toolbar_title_info_event)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        info_description.setText(event.description)
        info_user.setText(event.user.name)
        info_repeated.setText(buildRepeatText(event))
        presenter.formatDate(event.metaData.startDateTime)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String) {
        info_date.setText("$formattedDayOfMonth $formattedMonth $formattedYear")
    }

    private fun buildRepeatText(event: Event): String {
        with(event.metaData.recurrence) {
            return if (this is EventRecurrence.SingleEvent) {
                getString(R.string.is_not_repeated)
            } else {
                val weeklyAddon = if (this is EventRecurrence.Weekly) " ${getString(R.string.on)} ${formatWeekdays(onDaysOfWeek)}" else ""
                val isRepeatedOn = getString(R.string.is_repeated)
                val msg = if (frequency > 1) {
                    "$isRepeatedOn $frequency ${getString(pluralName)}"
                } else {
                    "$isRepeatedOn ${getString(name)}"
                }.plus(weeklyAddon)
                msg
            }
        }
    }

    private fun formatWeekdays(daysOfWeek: List<Int>?): String {
        return daysOfWeek?.joinToString {
            when (it) {
                0 -> getString(R.string.repeat_mo)
                1 -> getString(R.string.repeat_tu)
                2 -> getString(R.string.repeat_we)
                3 -> getString(R.string.repeat_th)
                4 -> getString(R.string.repeat_fr)
                5 -> getString(R.string.repeat_sa)
                6 -> getString(R.string.repeat_su)
                else -> ""
            }
        }.orEmpty()
    }
}
