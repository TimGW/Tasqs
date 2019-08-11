package com.timgortworst.roomy.ui.features.event.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.data.utils.Constants
import com.timgortworst.roomy.ui.base.view.BaseActivity
import com.timgortworst.roomy.ui.features.event.adapter.SpinnerTaskAdapter
import com.timgortworst.roomy.ui.features.event.adapter.SpinnerUserAdapter
import com.timgortworst.roomy.ui.features.event.presenter.EventEditPresenter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_edit_event.*
import java.util.*
import javax.inject.Inject


class EventEditActivity : BaseActivity(), EventEditView, DatePickerDialog.OnDateSetListener {
    private var event: Event? = null
    private lateinit var spinnerAdapterTasks: SpinnerTaskAdapter
    private lateinit var spinnerAdapterUsers: SpinnerUserAdapter
    private lateinit var datePickerDialog: DatePickerDialog
    private var calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, Constants.DEFAULT_HOUR_OF_DAY_NOTIFICATION) // default of 20:00
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    @Inject
    lateinit var presenter: EventEditPresenter

    companion object {
        const val INTENT_EXTRA_EDIT_EVENT = "INTENT_EXTRA_EDIT_EVENT"

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

        event = intent.getParcelableExtra(INTENT_EXTRA_EDIT_EVENT)

        supportActionBar?.apply {
            title = getString(R.string.new_agenda_item)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        event?.let {
            supportActionBar?.title = getString(R.string.edit_event, it.eventCategory.name)
            agenda_item_date_input.setText(it.eventMetaData.nextEventDate.toString())
        }

        presenter.getCategories()
        presenter.getUsers()
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

    private fun setupEventRepeatSpinner() {
        spinner_repeat.adapter = ArrayAdapter<EventMetaData.RepeatingInterval>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                EventMetaData.RepeatingInterval.values().dropWhile { it == EventMetaData.RepeatingInterval.SINGLE_EVENT }
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
                val eventMetaData = if (event_repeat_checkbox.isChecked) {
                    val repeatInterval = (spinner_repeat.selectedItem as EventMetaData.RepeatingInterval)
                    EventMetaData(nextEventDate = calendar.timeInMillis, repeatInterval = repeatInterval)
                } else {
                    EventMetaData(nextEventDate = calendar.timeInMillis, repeatInterval = EventMetaData.RepeatingInterval.SINGLE_EVENT)
                }

                presenter.createOrUpdateEvent(
                        event?.eventId,
                        (spinner_categories.selectedItem as Category),
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
        spinnerAdapterUsers = SpinnerUserAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                users
        )
        spinner_users.adapter = spinnerAdapterUsers
    }

    override fun presentCategoryList(tasks: MutableList<Category>) {
        spinnerAdapterTasks = SpinnerTaskAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                tasks
        )
        spinner_categories.adapter = spinnerAdapterTasks
    }

    private fun setupCalenderDialog() {
        datePickerDialog = DatePickerDialog(
                this, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis
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
