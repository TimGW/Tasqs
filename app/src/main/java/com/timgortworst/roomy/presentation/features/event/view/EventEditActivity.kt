package com.timgortworst.roomy.presentation.features.event.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.get
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventRecurrence
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.domain.utils.clearFocus
import com.timgortworst.roomy.domain.utils.toIntOrOne
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.event.adapter.SpinnerUserAdapter
import com.timgortworst.roomy.presentation.features.event.presenter.EventEditPresenter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_edit_event.*
import kotlinx.android.synthetic.main.layout_recurrence_picker.*
import kotlinx.android.synthetic.main.layout_week_picker.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField
import javax.inject.Inject


class EventEditActivity : BaseActivity(), EventEditView, DatePickerDialog.OnDateSetListener {
    private lateinit var event: Event
    private lateinit var userAdapter: SpinnerUserAdapter
    private lateinit var popup: PopupMenu
    private var recurrenceMenuId = R.id.days

    @Inject
    lateinit var presenter: EventEditPresenter

    companion object {
        const val INTENT_EXTRA_EDIT_EVENT = "INTENT_EXTRA_EDIT_EVENT"

        fun start(context: AppCompatActivity) {
            val intent = Intent(context, EventEditActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.slide_up, R.anim.stay)
        }

        fun start(context: AppCompatActivity, event: Event) {
            val intent = Intent(context, EventEditActivity::class.java)
            intent.putExtra(INTENT_EXTRA_EDIT_EVENT, event)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.slide_up, R.anim.stay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        supportActionBar?.apply {
            title = getString(R.string.toolbar_title_new_event)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        event = (intent.getParcelableExtra(INTENT_EXTRA_EDIT_EVENT) as? Event) ?: Event()
        setupUI()
    }

    private fun setupUI() {
        popup = PopupMenu(this, recurrence_type_button)
        inflatePopUpMenu(R.menu.recurrence_popup_menu)

        setupListeners()
        setupUserSpinner()

        supportActionBar?.title = getString(R.string.toolbar_title_edit_event, event.description)
        event_description.setText(event.description)
        presenter.formatDate(event.metaData.startDateTime)
        event_repeat_checkbox.isChecked = event.metaData.recurrence !is EventRecurrence.SingleEvent

        event.metaData.recurrence.apply {
            when (this) {
                is EventRecurrence.Daily -> {
                    recurrence_frequency.setText(everyXDays.toString())
                }
                is EventRecurrence.Weekly -> {
                    recurrence_frequency.setText(everyXWeeks.toString())
                    onDaysOfWeek?.forEach {
                        weekday_button_group.check(weekday_button_group[it].id)
                    }
                }
                is EventRecurrence.Monthly -> {
                    recurrence_frequency.setText(everyXMonths.toString())
                }
                is EventRecurrence.Annually -> {
                    recurrence_frequency.setText(everyXYears.toString())
                }
            }
        }
    }

    private fun setupListeners() {
        event_description.doAfterTextChanged {
            if (it?.isNotEmpty() == true) event_description_hint.error = null
            event.description = event_description.text.toString()
        }

        agenda_item_date_input.setOnClickListener {
            event.metaData.startDateTime.let {
                DatePickerDialog(
                        this, this,
                        it.year,
                        it.monthValue - 1,
                        it.dayOfMonth
                ).apply {
                    datePicker.minDate = Instant.now().toEpochMilli()
                    show()
                }
            }
        }

        event_repeat_checkbox.setOnCheckedChangeListener { _: CompoundButton, ischecked: Boolean ->
            clearFocus(recurrence_frequency)
            clearFocus(event_description)

            event_repeat_view.visibility = if (ischecked) {
                event.metaData.recurrence = EventRecurrence.Daily(recurrence_frequency.text.toIntOrOne())
                View.VISIBLE
            } else {
                clearRecurrence()
                View.GONE
            }
        }

        recurrence_frequency.setOnFocusChangeListener { v, hasFocus ->
            presenter.disableEmptyInput(recurrence_frequency, hasFocus)
        }

        recurrence_frequency.doAfterTextChanged {
            presenter.disableInputZero(it)
            presenter.checkForPluralRecurrenceType(recurrence_frequency.text.toString())
            recurrence_type_button?.text = popup.menu.findItem(recurrenceMenuId).title
        }

        recurrence_type_button.setOnClickListener {
            clearFocus(recurrence_frequency)
            clearFocus(event_description)

            popup.setOnMenuItemClickListener { item ->
                recurrenceMenuId = item.itemId
                recurrence_type_button?.text = popup.menu.findItem(item.itemId).title
                recurrence_week_picker?.visibility = if (item.itemId == R.id.weeks) View.VISIBLE else View.GONE
                true
            }
            popup.show()
        }

        weekday_button_group.addOnButtonCheckedListener { group, checkedId, isChecked ->
            clearFocus(recurrence_frequency)
            clearFocus(event_description)
        }
    }

    private fun clearRecurrence() {
        event.metaData.recurrence = EventRecurrence.SingleEvent
        weekday_button_group.clearChecked()
        recurrence_frequency.setText("1")
        recurrence_type_button?.text = popup.menu.findItem(R.id.days).title
        recurrence_week_picker?.visibility = View.GONE
    }

    private fun setRecurrence(frequency: Int,
                              recurrenceType: Int,
                              selectedWeekDays: List<Int>): EventRecurrence {
        return when (recurrenceType) {
            R.id.days -> EventRecurrence.Daily(frequency)
            R.id.weeks -> EventRecurrence.Weekly(frequency, selectedWeekDays)
            R.id.months -> EventRecurrence.Monthly(frequency)
            R.id.year -> EventRecurrence.Annually(frequency)
            else -> EventRecurrence.SingleEvent
        }
    }

    private fun setupUserSpinner() {
        userAdapter = SpinnerUserAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mutableListOf()
        )
        spinner_users.adapter = userAdapter
        presenter.getUsers()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_edit_done -> {
                event.metaData.recurrence = setRecurrence(
                        recurrence_frequency.text.toIntOrOne(),
                        recurrenceMenuId,
                        getSelectedWeekdays()
                )
                event.user = spinner_users.selectedItem as User
                presenter.editEventDone(event)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getSelectedWeekdays(): List<Int> {
        return weekday_button_group
                .checkedButtonIds
                .map { buttonId ->
                    val btn = weekday_button_group.findViewById<MaterialButton>(buttonId)
                    weekday_button_group.indexOfChild(btn)
                } // map checked buttons to weekday index 0..6 (mo - su)
    }

    private fun checkWeekdayToday() {
        val weekDay = ZonedDateTime.now().get(ChronoField.DAY_OF_WEEK) - 1
        weekday_button_group?.check(weekday_button_group[weekDay].id)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.stay, R.anim.slide_down)
    }

    override fun presentUserList(users: MutableList<User>) {
        userAdapter.addAll(users)
        userAdapter.notifyDataSetChanged()

        if (intent.hasExtra(INTENT_EXTRA_EDIT_EVENT)) {
            val userPos = userAdapter.getPosition(event.user)
            spinner_users.setSelection(userPos)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        event.metaData.startDateTime = ZonedDateTime.of(
                LocalDate.of(year, month + 1, dayOfMonth),
                LocalTime.NOON,
                ZoneId.systemDefault())
        presenter.formatDate(event.metaData.startDateTime)
    }

    override fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String) {
        agenda_item_date_input.setText("$formattedDayOfMonth $formattedMonth $formattedYear")
    }

    override fun presentEmptyDescriptionError(errorMessage: Int) {
        event_description_hint.error = getString(errorMessage)
    }

    override fun inflatePopUpMenu(menuId: Int) {
        popup.menu.clear()
        popup.menuInflater.inflate(menuId, popup.menu)
    }

    override fun finishActivity() {
        finish()
    }
}
