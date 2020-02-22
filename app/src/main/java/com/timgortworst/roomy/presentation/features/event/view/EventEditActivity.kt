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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.EventRecurrence
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.utils.clearFocus
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.event.presenter.EventEditPresenter
import com.timgortworst.roomy.presentation.features.main.view.MainActivity
import kotlinx.android.synthetic.main.activity_edit_event.*
import kotlinx.android.synthetic.main.layout_recurrence_picker.*
import kotlinx.android.synthetic.main.layout_week_picker.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField

class EventEditActivity : BaseActivity(), EventEditView, DatePickerDialog.OnDateSetListener {
    private var userList: MutableList<User> = mutableListOf()
    private val presenter: EventEditPresenter by inject {
        parametersOf(this)
    }
    private var event: Event = Event()
    private lateinit var userAdapter: ArrayAdapter<String>
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

        userAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mutableListOf(""))
        spinner_users.adapter = userAdapter
        presenter.getUsers()

        recurrenceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, recurrences.map { getString(it.name) })
        spinner_recurrence.adapter = recurrenceAdapter

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
                event.user = userFromSelection() ?: run {
                    val errorText = spinner_users.selectedView as TextView
                    errorText.setTextColor(ContextCompat.getColor(this, R.color.color_error))
                    errorText.text = getString(R.string.user_picker_error)
                    return false
                }
                event.metaData.recurrence = recurrenceFromSelection()
                presenter.editEventDone(event)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun userFromSelection(): User? {
        return if ((spinner_users.selectedItem as? String).isNullOrEmpty()) {
            null
        } else {
            userList[spinner_users.selectedItemPosition]
        }
    }

    private fun recurrenceFromSelection(): EventRecurrence {
        if (!event_repeat_checkbox.isChecked) return EventRecurrence.SingleEvent()
        val freq = recurrence_frequency.text.toString().toIntOrNull() ?: 1

        return when (recurrences[spinner_recurrence.selectedItemPosition]) {
            is EventRecurrence.Daily -> EventRecurrence.Daily(freq)
            is EventRecurrence.Weekly -> {
                val currentWeekday = ZonedDateTime.now().get(ChronoField.DAY_OF_WEEK)
                val weekdays = getSelectedWeekdays().ifEmpty {
                    listOf(currentWeekday)
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
                    weekday_button_group.indexOfChild(btn) + 1
                } // map checked buttons to weekday index 0..6 (mo - su)
    }

    override fun presentUserList(users: MutableList<User>) {
        this.userList = users
        userAdapter.clear()
        userAdapter.addAll(userList.map { it.name })
        userAdapter.notifyDataSetChanged()

        if (intent.hasExtra(INTENT_EXTRA_EDIT_EVENT)) {
            val index = userList.indexOfFirst { it.name == event.user.name }
            spinner_users.setSelection(index)
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
