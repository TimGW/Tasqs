package com.timgortworst.roomy.presentation.features.task.view

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
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.utils.clearFocus
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.task.presenter.TaskEditPresenter
import com.timgortworst.roomy.presentation.features.main.MainActivity
import kotlinx.android.synthetic.main.activity_edit_task.*
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

class TaskEditActivity : BaseActivity(), TaskEditView, DatePickerDialog.OnDateSetListener {
    private val presenter: TaskEditPresenter by inject { parametersOf(this) }
    private var userList: List<User> = listOf()
    private lateinit var task: Task
    private lateinit var recurrenceAdapter: ArrayAdapter<String>
    private val recurrences = listOf(
        TaskRecurrence.Daily(),
        TaskRecurrence.Weekly(),
        TaskRecurrence.Monthly(),
        TaskRecurrence.Annually()
    )

    companion object {
        const val INTENT_EXTRA_EDIT_TASK = "INTENT_EXTRA_EDIT_TASK"

        fun start(context: AppCompatActivity, task: Task? = null) {
            val intent = Intent(context, TaskEditActivity::class.java)
            task?.let { intent.putExtra(INTENT_EXTRA_EDIT_TASK, it) }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        task = intent.getParcelableExtra(INTENT_EXTRA_EDIT_TASK) as? Task ?: Task()

        setupUI()
    }

    private fun setupUI() {
        setupToolbar()
        setupListeners()

        presenter.getUsers()

        recurrenceAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            recurrences.map { getString(it.name) }
        )
        spinner_recurrence.adapter = recurrenceAdapter

        presenter.formatDate(task.metaData.startDateTime)

        if (intent.hasExtra(INTENT_EXTRA_EDIT_TASK) &&
            intent.getParcelableExtra(INTENT_EXTRA_EDIT_TASK) as? Task != null
        ) {
            setupEditUI()
        }
    }

    private fun setupEditUI() {
        supportActionBar?.title = getString(R.string.toolbar_title_edit_task, task.description)
        task_description.setText(task.description)
        presenter.formatDate(task.metaData.startDateTime)

        val isRepeating = task.metaData.recurrence !is TaskRecurrence.SingleTask
        task_repeat_checkbox.isChecked = isRepeating

        val index = recurrences.indexOfFirst { it.name == task.metaData.recurrence.name }
        spinner_recurrence.setSelection(index)
        task_repeat_view.visibility = if (isRepeating) View.VISIBLE else View.GONE
        val freq = task.metaData.recurrence.frequency.toString()
        recurrence_frequency.setText(freq)
        (task.metaData.recurrence as? TaskRecurrence.Weekly)?.let { weekly ->
            weekly.onDaysOfWeek.forEach { index ->
                weekday_button_group.check(weekday_button_group[index].id)
            }
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = getString(R.string.toolbar_title_new_task)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupListeners() {
        task_description.doAfterTextChanged {
            if (it?.isNotEmpty() == true) task_description_hint.error = null
            task.description = task_description.text.toString()
        }

        task_date_input.setOnClickListener {
            task.metaData.startDateTime.let {
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

        task_repeat_checkbox.setOnCheckedChangeListener { _, isChecked ->
            clearFocus(recurrence_frequency)
            clearFocus(task_description)

            // todo animate down
            task_repeat_view.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        recurrence_frequency.setOnFocusChangeListener { v, hasFocus ->
            presenter.disableEmptyInput(recurrence_frequency, hasFocus)
        }

        recurrence_frequency.doAfterTextChanged {
            presenter.disableInputZero(it)
            presenter.checkForPluralRecurrenceSpinner(recurrence_frequency.text.toString())
        }

        spinner_recurrence.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                clearFocus(recurrence_frequency)
                clearFocus(task_description)
                recurrence_week_picker?.visibility =
                    if (recurrenceFromSelection() is TaskRecurrence.Weekly) View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        weekday_button_group.addOnButtonCheckedListener { group, checkedId, isChecked ->
            clearFocus(recurrence_frequency)
            clearFocus(task_description)
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
                val result = spinner_users.selectedItemPosition
                if (result != -1) task.user = userList[result]
                task.metaData.recurrence = recurrenceFromSelection()
                presenter.editTaskDone(task) //todo update tasklist
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun recurrenceFromSelection(): TaskRecurrence {
        if (!task_repeat_checkbox.isChecked) return TaskRecurrence.SingleTask()
        val freq = recurrence_frequency.text.toString().toIntOrNull() ?: 1

        return when (recurrences[spinner_recurrence.selectedItemPosition]) {
            is TaskRecurrence.Daily -> TaskRecurrence.Daily(freq)
            is TaskRecurrence.Weekly -> {
                val currentWeekday = ZonedDateTime.now().get(ChronoField.DAY_OF_WEEK)
                val weekdays = getSelectedWeekdays().ifEmpty {
                    listOf(currentWeekday)
                }
                TaskRecurrence.Weekly(freq, weekdays)
            }
            is TaskRecurrence.Monthly -> TaskRecurrence.Monthly(freq)
            is TaskRecurrence.Annually -> TaskRecurrence.Annually(freq)
            is TaskRecurrence.SingleTask -> TaskRecurrence.SingleTask(freq)
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

    override fun presentUserList(filteredUserList: List<User>) {
        this.userList = filteredUserList

        user_group.visibility = View.VISIBLE

        val userAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            filteredUserList.map { it.name }
        )
        spinner_users.adapter = userAdapter

        if (intent.hasExtra(INTENT_EXTRA_EDIT_TASK)) {
            val index = filteredUserList.indexOf(task.user)
            spinner_users.setSelection(index)
        }
    }

    override fun presentCurrentUser(currentUser: User?) {
        currentUser?.let { task.user = it }
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
        task.metaData.startDateTime = ZonedDateTime.of(
            LocalDate.of(year, month + 1, dayOfMonth),
            LocalTime.NOON,
            ZoneId.systemDefault()
        )
        presenter.formatDate(task.metaData.startDateTime)
    }

    override fun presentFormattedDate(
        formattedDayOfMonth: String,
        formattedMonth: String?,
        formattedYear: String
    ) {
        task_date_input.setText("$formattedDayOfMonth $formattedMonth $formattedYear")
    }

    override fun presentEmptyDescriptionError(errorMessage: Int) {
        task_description_hint.error = getString(errorMessage)
    }

    override fun finishActivity() {
        navigateUpTo(Intent(this, MainActivity::class.java))
    }
}
