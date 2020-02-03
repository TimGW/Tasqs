package com.timgortworst.roomy.presentation.features.event.view

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
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.event.adapter.SpinnerUserAdapter
import com.timgortworst.roomy.presentation.features.event.presenter.EventEditPresenter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_edit_event.*
import org.threeten.bp.LocalDate
import javax.inject.Inject


class EventEditActivity : BaseActivity(), EventEditView, DatePickerDialog.OnDateSetListener {
    private var event: Event? = null
    private lateinit var spinnerAdapterUsers: SpinnerUserAdapter
    private lateinit var spinnerAdapterRepeat: ArrayAdapter<EventMetaData.EventInterval>
    private lateinit var datePickerDialog: DatePickerDialog
    private var localDate = LocalDate.now()

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

        supportActionBar?.apply {
            title = getString(R.string.toolbar_title_new_event)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        setupUserSpinner()
        setupEventRepeatSpinner()
        setupCalenderDialog()
        setupClickListeners()

        presenter.getUsers()
        presenter.formatDateAndSetUI(localDate)

        if (intent.hasExtra(INTENT_EXTRA_EDIT_EVENT)) {
            event = (intent.getParcelableExtra(INTENT_EXTRA_EDIT_EVENT) as? Event)?.also {
                agenda_item_description_input.setText(it.description)
                localDate = it.eventMetaData.eventTimestamp.toLocalDate()
                supportActionBar?.title = getString(R.string.toolbar_title_edit_event, it.description)
                presenter.formatDateAndSetUI(localDate)
                event_repeat_checkbox.isChecked = it.eventMetaData.eventInterval != EventMetaData.EventInterval.SINGLE_EVENT
                val repeatPos = spinnerAdapterRepeat.getPosition(it.eventMetaData.eventInterval)
                spinner_repeat.setSelection(repeatPos)
            }
        }
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

    private fun setupUserSpinner() {
        spinnerAdapterUsers = SpinnerUserAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mutableListOf()
        )
        spinner_users.adapter = spinnerAdapterUsers
    }


    private fun setupCalenderDialog() {
        datePickerDialog = DatePickerDialog(
                this, this,
                localDate.year,
                localDate.monthValue - 1,
                localDate.dayOfMonth
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
    }

    private fun setupEventRepeatSpinner() {
        spinnerAdapterRepeat = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                EventMetaData.EventInterval.values().dropWhile { it == EventMetaData.EventInterval.SINGLE_EVENT }
        )
        spinner_repeat.adapter = spinnerAdapterRepeat
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
                        event_repeat_checkbox.isChecked,
                        spinner_repeat?.selectedItem as? EventMetaData.EventInterval,
                        localDate,
                        event?.eventId,
                        spinner_users.selectedItem as User,
                        agenda_item_description_input.text.toString())

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
        spinnerAdapterUsers.addAll(users)
        spinnerAdapterUsers.notifyDataSetChanged()

        if (intent.hasExtra(INTENT_EXTRA_EDIT_EVENT)) {
            val userPos = spinnerAdapterUsers.getPosition(event?.user)
            spinner_users.setSelection(userPos)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        localDate = LocalDate.of(year, month + 1, dayOfMonth)
        presenter.formatDateAndSetUI(localDate)
    }

    override fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String) {
        agenda_item_date_input.setText("$formattedDayOfMonth $formattedMonth $formattedYear")
    }
}
