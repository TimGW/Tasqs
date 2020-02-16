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
import com.timgortworst.roomy.data.model.EventInterval
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.domain.utils.clearFocus
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.event.adapter.SpinnerUserAdapter
import com.timgortworst.roomy.presentation.features.event.presenter.EventEditPresenter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_edit_event.*
import kotlinx.android.synthetic.main.layout_recurrence_picker.*
import kotlinx.android.synthetic.main.layout_week_picker.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField
import javax.inject.Inject
import kotlin.properties.Delegates


class EventEditActivity : BaseActivity(), EventEditView, DatePickerDialog.OnDateSetListener {
    private var event: Event? = null
    private lateinit var userAdapter: SpinnerUserAdapter
    private lateinit var popup: PopupMenu
    private var startDate by Delegates.observable(LocalDate.now()) { d, old, new ->
        presenter.formatDate(new)
    }
    private var selectedRecurrenceType: Int by Delegates.observable(NO_RECURRENCE) { d, old, new ->
        updateRecurrenceButtonText(new)
        recurrence_week_picker?.visibility = if (new == R.id.weeks) View.VISIBLE else View.GONE
        if (new == NO_RECURRENCE) {
            weekday_button_group.clearChecked()
            recurrence_frequency.setText("1")
            event_repeat_checkbox.isChecked = false
        } else {
            if (weekday_button_group.checkedButtonIds.isEmpty()) checkWeekdayToday()
            event_repeat_checkbox.isChecked = true
        }
    }

    @Inject
    lateinit var presenter: EventEditPresenter

    companion object {
        const val INTENT_EXTRA_EDIT_EVENT = "INTENT_EXTRA_EDIT_EVENT"
        const val NO_RECURRENCE = -1

        fun start(context: AppCompatActivity) {
            val intent = Intent(context, EventEditActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.slide_up, R.anim.stay)
        }

        fun start(context: AppCompatActivity, agendaEvent: Event) {
            val intent = Intent(context, EventEditActivity::class.java)
            intent.putExtra(INTENT_EXTRA_EDIT_EVENT, agendaEvent)
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

        setupUI()

        event = (intent.getParcelableExtra(INTENT_EXTRA_EDIT_EVENT) as? Event)?.apply {
            preFillUI(this)
        }
    }

    private fun preFillUI(event: Event) {
        supportActionBar?.title = getString(R.string.toolbar_title_edit_event, event.description)
        event_description.setText(event.description)
        startDate  = event.eventMetaData.eventTimestamp.toLocalDate()

        event.eventMetaData.eventInterval.apply {
            weekday_button_group.clearChecked()
            selectedRecurrenceType = when (this) {
                is EventInterval.SingleEvent -> NO_RECURRENCE
                is EventInterval.Daily -> {
                    recurrence_frequency.setText(everyXDays.toString())
                    R.id.days
                }
                is EventInterval.Weekly -> {
                    recurrence_frequency.setText(everyXWeeks.toString())
                    onDaysOfWeek.forEach {
                        weekday_button_group.check(weekday_button_group[it].id)
                    }
                    R.id.weeks
                }
                is EventInterval.Monthly -> {
                    recurrence_frequency.setText(everyXMonths.toString())
                    R.id.months
                }
                is EventInterval.Annually -> {
                    recurrence_frequency.setText(everyXYears.toString())
                    R.id.year
                }
            }
        }
    }

    private fun setupUI() {
        startDate = ZonedDateTime.now().toLocalDate()

        popup = PopupMenu(this, recurrence_type_button)
        inflatePopUpMenu(R.menu.recurrence_popup_menu)

        checkWeekdayToday()
        setupUserSpinner()
        setupListeners()
    }

    private fun checkWeekdayToday() {
        val weekDay = ZonedDateTime.now().get(ChronoField.DAY_OF_WEEK) - 1
        weekday_button_group?.check(weekday_button_group[weekDay].id)
    }

    private fun setupListeners() {
        agenda_item_date_input.setOnClickListener {
            DatePickerDialog(
                    this, this,
                    startDate.year,
                    startDate.monthValue - 1,
                    startDate.dayOfMonth
            ).apply {
                datePicker.minDate = Instant.now().toEpochMilli()
                show()
            }
        }

        weekday_button_group.addOnButtonCheckedListener { group, checkedId, isChecked ->
            clearFocus(recurrence_frequency)
            clearFocus(event_description)
        }

        event_repeat_checkbox.setOnCheckedChangeListener { _: CompoundButton, ischecked: Boolean ->
            clearFocus(recurrence_frequency)
            clearFocus(event_description)

            event_repeat_view.visibility = if (ischecked) {
                View.VISIBLE
            } else {
                selectedRecurrenceType = NO_RECURRENCE
                View.GONE
            }
        }

        recurrence_type_button.setOnClickListener {
            clearFocus(recurrence_frequency)
            clearFocus(event_description)

            popup.setOnMenuItemClickListener { item ->
                selectedRecurrenceType = item.itemId
                true
            }
            popup.show()
        }

        recurrence_frequency.setOnFocusChangeListener { v, hasFocus ->
            presenter.disableEmptyInput(recurrence_frequency, hasFocus)
        }

        event_description.doAfterTextChanged {
            if (it?.isNotEmpty() == true) event_description_hint.error = null
        }

        recurrence_frequency.doAfterTextChanged {
            presenter.disableInputZero(it)
            presenter.checkForPluralUI(recurrence_frequency.text.toString())
            updateRecurrenceButtonText(selectedRecurrenceType)
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
                presenter.editEventDone(
                        startDate,
                        event?.eventId,
                        spinner_users.selectedItem as User,
                        event_description.text.toString(),
                        recurrence_frequency.text.toString(),
                        selectedRecurrenceType,
                        weekday_button_group
                                .checkedButtonIds
                                .map { buttonId ->
                                    val btn = weekday_button_group.findViewById<MaterialButton>(buttonId)
                                    weekday_button_group.indexOfChild(btn)
                                } // map checked buttons to weekday index 0..6 (mo - su)
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateRecurrenceButtonText(currentSelectedMenuItemId: Int) {
        recurrence_type_button?.text = if (currentSelectedMenuItemId != NO_RECURRENCE) {
            popup.menu.findItem(currentSelectedMenuItemId).title
        } else {
            popup.menu.findItem(R.id.days).title
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.stay, R.anim.slide_down)
    }

    override fun presentUserList(users: MutableList<User>) {
        userAdapter.addAll(users)
        userAdapter.notifyDataSetChanged()

        if (intent.hasExtra(INTENT_EXTRA_EDIT_EVENT)) {
            val userPos = userAdapter.getPosition(event?.user)
            spinner_users.setSelection(userPos)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        startDate = LocalDate.of(year, month + 1, dayOfMonth)
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
