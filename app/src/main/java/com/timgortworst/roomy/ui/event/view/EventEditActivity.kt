package com.timgortworst.roomy.ui.event.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.ui.event.adapter.SpinnerTaskAdapter
import com.timgortworst.roomy.ui.event.adapter.SpinnerUserAdapter
import com.timgortworst.roomy.ui.event.presenter.EventEditPresenter
import com.timgortworst.roomy.utils.Constants.INTENT_EXTRA_EDIT_HOUSEHOLD_TASK
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_edit_agenda_item.*
import java.util.*
import javax.inject.Inject


class EventEditActivity : AppCompatActivity(), EventEditView, DatePickerDialog.OnDateSetListener {
    private lateinit var agendaEvent: Event
    private lateinit var spinnerAdapterTasks: SpinnerTaskAdapter
    private lateinit var spinnerAdapterUsers: SpinnerUserAdapter
    private lateinit var datePickerDialog: DatePickerDialog
    private var calendar = Calendar.getInstance()

    @Inject
    lateinit var presenter: EventEditPresenter

    companion object {
        fun start(context: AppCompatActivity) {
            val intent = Intent(context, EventEditActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.slide_up, R.anim.stay)
        }

        fun start(context: AppCompatActivity, agendaEvent: Event) {
            val intent = Intent(context, EventEditActivity::class.java)
            intent.putExtra(INTENT_EXTRA_EDIT_HOUSEHOLD_TASK, agendaEvent)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.slide_up, R.anim.stay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_agenda_item)

        agendaEvent = intent.getParcelableExtra(INTENT_EXTRA_EDIT_HOUSEHOLD_TASK) ?: Event()

        supportActionBar?.apply {
            title = "Nieuw agenda item"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        agendaEvent.let {
            if (it.agendaId.isNotEmpty()) {
                supportActionBar?.title = "Edit  ${agendaEvent.eventCategory.name}"
                agenda_item_date_input.setText(agendaEvent.eventMetaData.repeatStartDate.toString())
            }
        }

        setupEventCategorySpinner()
        setupUsersSpinner()
        setupEventRepeatSpinner()
        setupCalenderDialog()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        agenda_item_date_input.setOnClickListener {
            datePickerDialog.show()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
        }

        event_repeat_checkbox.setOnCheckedChangeListener { _: CompoundButton, ischecked: Boolean ->
            if (ischecked) {
                event_repeat_view.visibility = View.VISIBLE
            } else {
                event_repeat_view.visibility = View.GONE
            }
        }
    }

    private fun setupEventCategorySpinner() {
        spinnerAdapterTasks = SpinnerTaskAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf()
        )
        spinner_categories.adapter = spinnerAdapterTasks
        spinner_categories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }
        }

        presenter.fetchEventCategories()
    }

    private fun setupUsersSpinner() {
        spinnerAdapterUsers = SpinnerUserAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf()
        )
        spinner_users.adapter = spinnerAdapterUsers
        presenter.fetchUsers()
    }

    private fun setupEventRepeatSpinner() {
        spinner_repeat.adapter = ArrayAdapter<EventMetaData.RepeatingInterval>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            EventMetaData.RepeatingInterval.values()
        )
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
                val repeatInterval = (spinner_repeat.selectedItem as EventMetaData.RepeatingInterval)
                val eventMetaData =
                    EventMetaData(repeatStartDate = calendar.timeInMillis, repeatInterval = repeatInterval)

                presenter.insertOrUpdateEvent(
                    agendaEvent.agendaId,
                    (spinner_categories.selectedItem as EventCategory),
                    (spinner_users.selectedItem as User),
                    eventMetaData
                )

                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.stay, R.anim.slide_down)
    }

    override fun presentUserList(users: MutableList<User>) {
        for (user in users) {
            spinnerAdapterUsers.add(user)
        }
        spinnerAdapterUsers.notifyDataSetChanged()
    }

    override fun presentCategoryList(tasks: MutableList<EventCategory>) {
        for (task in tasks) {
            spinnerAdapterTasks.add(task)
        }
        spinnerAdapterTasks.notifyDataSetChanged()
    }

    private fun setupCalenderDialog() {
        datePickerDialog = DatePickerDialog(
            this, this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        presenter.formatDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        presenter.formatDate(year, month, dayOfMonth)
    }

    override fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String) {
        agenda_item_date_input.setText("$formattedDayOfMonth $formattedMonth $formattedYear")
    }
}
