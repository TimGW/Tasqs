package com.timgortworst.roomy.presentation.features.event.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.EventRecurrence
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.event.presenter.EventInfoPresenter
import com.timgortworst.roomy.presentation.features.main.view.MainActivity
import kotlinx.android.synthetic.main.activity_info_event.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class EventInfoActivity : BaseActivity(), EventInfoView {
    private val presenter: EventInfoPresenter by inject {
        parametersOf(this)
    }
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

        info_description.text = event.description
        info_user.text = event.user.name
        info_repeated.text = buildRepeatText(event)
        presenter.formatDate(event.metaData.startDateTime)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, MainActivity::class.java))
                true
            }
            R.id.action_go_to_edit -> {
                EventEditActivity.start(this, event)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.info_menu, menu)
        return true
    }

    override fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String) {
        info_date.text = "$formattedDayOfMonth $formattedMonth $formattedYear"
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
                1 -> getString(R.string.repeat_mo)
                2 -> getString(R.string.repeat_tu)
                3 -> getString(R.string.repeat_we)
                4 -> getString(R.string.repeat_th)
                5 -> getString(R.string.repeat_fr)
                6 -> getString(R.string.repeat_sa)
                7 -> getString(R.string.repeat_su)
                else -> "?"
            }
        }.orEmpty()
    }
}
