package com.timgortworst.roomy.presentation.features.event.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
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
import com.timgortworst.roomy.presentation.features.main.view.MainActivity
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
    @Inject
    lateinit var presenter: EventEditPresenter

    private var event: Event = Event()
    private lateinit var userAdapter: SpinnerUserAdapter
    private lateinit var recurrenceAdapter: ArrayAdapter<String>

    companion object {
        const val INTENT_EXTRA_EDIT_EVENT = "INTENT_EXTRA_EDIT_EVENT"
        private val recurrences = listOf(
                EventRecurrence.Daily(),
                EventRecurrence.Weekly(),
                EventRecurrence.Monthly(),
                EventRecurrence.Annually())

        fun start(context: AppCompatActivity, event: Event? = null) {
            val intent = Intent(context, EventEditActivity::class.java)
            event?.let { intent.putExtra(INTENT_EXTRA_EDIT_EVENT, it) }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        if (isInEditMode()) event = intent.getParcelableExtra(INTENT_EXTRA_EDIT_EVENT) as Event

        setupUI()
        if (isInEditMode()) setupEditUI()
    }

    private fun isInEditMode() = intent.hasExtra(INTENT_EXTRA_EDIT_EVENT) &&
            intent.getParcelableExtra(INTENT_EXTRA_EDIT_EVENT) as? Event != null

    private fun setupUI() {
        setupToolbar()
        setupListeners()
        setupUserSpinner()
        setupRecurrenceSpinner()

        presenter.formatDate(event.metaData.startDateTime)
    }

    private fun setupEditUI() {
        supportActionBar?.title = getString(R.string.toolbar_title_edit_event, event.description)
        event_description.setText(event.description)
        presenter.formatDate(event.metaData.startDateTime)

        val isRepeating = event.metaData.recurrence !is EventRecurrence.SingleEvent
        event_repeat_checkbox.isChecked = isRepeating

        val index = recurrences.indexOfFirst { it.name == event.metaData.recurrence.name }
        spinner_recurrence.setSelection(index)
        event_repeat_view.visibility = if (isRepeating) View.VISIBLE else View.GONE
        val freq = event.metaData.recurrence.frequency.toString()
        recurrence_frequency.setText(freq)
        (event.metaData.recurrence as? EventRecurrence.Weekly)?.let { weekly ->
            weekly.onDaysOfWeek?.forEach { index ->
                weekday_button_group.check(weekday_button_group[index].id)
            }
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = getString(R.string.toolbar_title_new_event)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
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

        event_repeat_checkbox.setOnCheckedChangeListener { _, isChecked ->
            clearFocus(recurrence_frequency)
            clearFocus(event_description)

            event_repeat_view.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        recurrence_frequency.setOnFocusChangeListener { v, hasFocus ->
            presenter.disableEmptyInput(recurrence_frequency, hasFocus)
        }

        recurrence_frequency.doAfterTextChanged {
            presenter.disableInputZero(it)
            presenter.checkForPluralRecurrenceSpinner(recurrence_frequency.text.toString())
        }

        spinner_recurrence.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                clearFocus(recurrence_frequency)
                clearFocus(event_description)
                recurrence_week_picker?.visibility = if (recurrenceFromSelection() is EventRecurrence.Weekly) View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        weekday_button_group.addOnButtonCheckedListener { group, checkedId, isChecked ->
            clearFocus(recurrence_frequency)
            clearFocus(event_description)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, MainActivity::class.java))
                true
            }
            R.id.action_edit_done -> {
                event.user = spinner_users.selectedItem as User
                event.metaData.recurrence = recurrenceFromSelection()
                presenter.editEventDone(event)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun recurrenceFromSelection(): EventRecurrence {
        if (!event_repeat_checkbox.isChecked) return EventRecurrence.SingleEvent()
        val freq = recurrence_frequency.text.toIntOrOne()

        return when (recurrences[spinner_recurrence.selectedItemPosition]) {
            is EventRecurrence.Daily -> EventRecurrence.Daily(freq)
            is EventRecurrence.Weekly -> {
                val weekdays = getSelectedWeekdays().ifEmpty {
                    listOf(ZonedDateTime.now().get(ChronoField.DAY_OF_WEEK) - 1)
                }
                EventRecurrence.Weekly(freq, weekdays)
            }
            is EventRecurrence.Monthly -> EventRecurrence.Monthly(freq)
            is EventRecurrence.Annually -> EventRecurrence.Annually(freq)
            is EventRecurrence.SingleEvent -> EventRecurrence.SingleEvent(freq)
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

    private fun setupUserSpinner() {
        userAdapter = SpinnerUserAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mutableListOf()
        )
        spinner_users.adapter = userAdapter
        presenter.getUsers()
    }

    private fun setupRecurrenceSpinner() {
        recurrenceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, recurrences.map { getString(it.name) })
        recurrenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_recurrence.adapter = recurrenceAdapter
    }

    override fun presentUserList(users: MutableList<User>) {
        userAdapter.addAll(users)
        userAdapter.notifyDataSetChanged()

        if (intent.hasExtra(INTENT_EXTRA_EDIT_EVENT)) {
            val userPos = userAdapter.getPosition(event.user)
            spinner_users.setSelection(userPos)
        }
    }

    override fun setPluralSpinner() {
        recurrenceAdapter.clear()
        recurrenceAdapter.addAll(recurrences.map { getString(it.pluralName) })
        recurrenceAdapter.notifyDataSetChanged()
    }

    override fun setSingularSpinner() {
        recurrenceAdapter.clear()
        recurrenceAdapter.addAll(recurrences.map { getString(it.name) })
        recurrenceAdapter.notifyDataSetChanged()
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

    override fun finishActivity() {
        finish()
    }
}
