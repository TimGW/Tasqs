package com.timgortworst.roomy.presentation.features.task.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.ActivityEditTaskBinding
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.domain.model.TaskUser
import com.timgortworst.roomy.presentation.base.clearFocus
import com.timgortworst.roomy.presentation.base.snackbar
import com.timgortworst.roomy.presentation.base.model.EventObserver
import com.timgortworst.roomy.presentation.features.main.MainActivity
import com.timgortworst.roomy.presentation.features.task.viewmodel.TaskEditViewModel
import org.koin.android.ext.android.inject
import org.threeten.bp.*
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

class TaskEditActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val task: Task by lazy {
        intent.getParcelableExtra(INTENT_EXTRA_EDIT_TASK) as? Task ?: Task()
    }
    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var recurrenceAdapter: ArrayAdapter<String>
    private val isEditMode: Boolean by lazy { intent.hasExtra(INTENT_EXTRA_EDIT_TASK) }
    private val viewModel: TaskEditViewModel by inject()
    private val recurrences = listOf(
        TaskRecurrence.Daily(),
        TaskRecurrence.Weekly(),
        TaskRecurrence.Monthly(),
        TaskRecurrence.Annually()
    )

    companion object {
        const val INTENT_EXTRA_EDIT_TASK = "INTENT_EXTRA_EDIT_TASK"

        fun intentBuilder(context: Context, task: Task? = null): Intent {
            val intent = Intent(context, TaskEditActivity::class.java)
            task?.let { intent.putExtra(INTENT_EXTRA_EDIT_TASK, it) }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(task.description)

        setupPageElements(task)

        viewModel.actionDone.observe(this, EventObserver {
            binding.progressBar.visibility = View.INVISIBLE
            when (it) {
                Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> navigateUpTo(parentActivityIntent)
                is Response.Error -> presentError(R.string.error_generic)
                is Response.Empty -> binding.taskDescriptionHint.error = getString(it.msg)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_done -> {
                viewModel.taskDoneClicked(task.apply {
                    // collect values for recurrence only when user is done, since it's a complex object
                    metaData.recurrence = recurrenceFromSelection()
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar(taskDescription: String) {
        supportActionBar?.apply {
            title = if (isEditMode) {
                getString(R.string.toolbar_title_edit_task, taskDescription)
            } else {
                getString(R.string.toolbar_title_new_task)
            }
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupPageElements(task: Task) {
        setupTaskDescription(task.description)
        setupTaskUser(task.user)
        setupTaskDate(task.metaData.startDateTime)
        setupTaskTime(task.metaData.startDateTime)
        setupTaskRepeatCheckbox(task.metaData.recurrence)
        setupTaskFrequency(task.metaData.recurrence.frequency)
        setupTaskRecurrence(task.metaData.recurrence)
    }

    private fun setupTaskDescription(taskDescription: String) {
        // set initial value
        binding.taskDescription.setText(taskDescription)

        // set listener
        binding.taskDescription.doAfterTextChanged {
            if (it?.isNotBlank() == true) binding.taskDescriptionHint.error = null
            task.description = binding.taskDescription.text.toString()
        }
    }

    private fun setupTaskUser(user: TaskUser) {
        // retrieve all users for this household
        viewModel.allUsersLiveData.observe(this, Observer { response ->
            binding.progressBar.visibility = View.INVISIBLE
            when (response) {
                Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> {
                    val currentUser = response.data?.find {
                        it.userId == FirebaseAuth.getInstance().currentUser?.uid
                    } ?: return@Observer

                    if (response.data.filterNot { it.userId == currentUser.userId }.isEmpty()) {
                        // assign current user to the task, since there is only 1 user in house
                        task.user = TaskUser(currentUser.userId, currentUser.name)
                    } else {
                        val userList = response.data.map { TaskUser(it.userId, it.name) }
                        binding.userGroup.visibility = View.VISIBLE

                        binding.spinnerUsers.adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            userList.map { it.name }
                        )
                        binding.spinnerUsers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {}

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                task.user = userList[position]
                            }
                        }

                        if (isEditMode) {
                            val activeUser = userList.indexOf(user)
                            binding.spinnerUsers.setSelection(activeUser)
                        }
                    }
                }
                is Response.Error -> presentError(R.string.users_loading_error)
            }
        })
    }

    private fun setupTaskDate(taskDateTime: ZonedDateTime) {
        // set initial value
        binding.taskDateInput.setText(formatDate(taskDateTime))

        // set listener
        binding.taskDateInput.setOnClickListener {
            task.metaData.startDateTime.let {
                DatePickerDialog(
                    this, this,
                    it.year,
                    it.monthValue - 1,
                    it.dayOfMonth
                ).apply {
                    datePicker.minDate = ZonedDateTime.now().toInstant().toEpochMilli()
                    show()
                }
            }
        }
    }

    private fun setupTaskTime(taskDateTime: ZonedDateTime) {
        // set initial value
        binding.taskTimeInput.setText(formatTime(taskDateTime))

        // set listener
        binding.taskTimeInput.setOnClickListener {
            task.metaData.startDateTime.let {
                TimePickerDialog(
                    this, this,
                    it.hour,
                    it.minute,
                    true
                ).show()
            }
        }
    }

    private fun setupTaskRepeatCheckbox(taskRecurrence: TaskRecurrence) {
        // set initial value
        val isRepeating = taskRecurrence !is TaskRecurrence.SingleTask
        binding.taskRepeatCheckbox.isChecked = isRepeating
        binding.taskRepeatView.root.visibility = if (isRepeating) View.VISIBLE else View.GONE

        // set listener
        binding.taskRepeatCheckbox.setOnCheckedChangeListener { _, isChecked ->
            clearAllFocus()
            binding.taskRepeatView.root.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }

    private fun setupTaskFrequency(frequency: Int) {
        // set initial value
        binding.taskRepeatView.recurrenceFrequency.setText(frequency.toString())

        // set listeners
        binding.taskRepeatView.recurrenceFrequency.setOnFocusChangeListener { _, hasFocus ->
            binding.taskRepeatView.recurrenceFrequency.apply {
                if (text.toString().isBlank() && !hasFocus) setText("1")
            }
        }
        binding.taskRepeatView.recurrenceFrequency.doAfterTextChanged {
            // don't allow 0 as input
            it?.let {
                val input = it.toString()
                if (input.isNotEmpty() && input.first() == '0') {
                    it.replace(0, 1, "1")
                }
            }

            val frequency = binding.taskRepeatView.recurrenceFrequency.text.toString()
            recurrenceAdapter.clear()
            if (frequency.toIntOrNull()?.equals(1) == true || frequency.isBlank()) {
                recurrenceAdapter.addAll(recurrences.map { getString(it.name) })
            } else {
                recurrenceAdapter.addAll(recurrences.map { getString(it.pluralName) })
            }
            recurrenceAdapter.notifyDataSetChanged()
        }
    }

    private fun setupTaskRecurrence(taskRecurrence: TaskRecurrence) {
        // set adapter
        recurrenceAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            recurrences.map { getString(it.name) }
        )
        binding.taskRepeatView.spinnerRecurrence.adapter = recurrenceAdapter

        // set initial value
        val recurrenceIndex = recurrences.indexOfFirst { it.name == taskRecurrence.name }
        binding.taskRepeatView.spinnerRecurrence.setSelection(recurrenceIndex)
        (taskRecurrence as? TaskRecurrence.Weekly)?.let { weekly ->
            val recurrenceWeekPicker = binding.taskRepeatView.recurrenceWeekPicker
            recurrenceWeekPicker.root.visibility = View.VISIBLE
            val weekdayButtonGroup = recurrenceWeekPicker.weekdayButtonGroup
            weekly.onDaysOfWeek.forEach { index ->
                weekdayButtonGroup.check(weekdayButtonGroup[index - 1].id)
            }
        }

        // set listeners
        binding.taskRepeatView.spinnerRecurrence.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    clearAllFocus()

                    val selectedRecurrence = recurrences[position]
                    binding.taskRepeatView.recurrenceWeekPicker.root.visibility =
                        if (selectedRecurrence is TaskRecurrence.Weekly) View.VISIBLE else View.GONE
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        binding.taskRepeatView.recurrenceWeekPicker.weekdayButtonGroup.addOnButtonCheckedListener { _, _, _ ->
            clearAllFocus()
        }
    }

    fun clearAllFocus() {
        clearFocus(binding.taskRepeatView.recurrenceFrequency)
        clearFocus(binding.taskDescription)
    }

    /** Provide TaskRecurrence from the current selected input */
    private fun recurrenceFromSelection(): TaskRecurrence {
        if (!binding.taskRepeatCheckbox.isChecked) return TaskRecurrence.SingleTask(1)
        val freq = binding.taskRepeatView.recurrenceFrequency.text.toString().toIntOrNull() ?: 1
        return when (recurrences[binding.taskRepeatView.spinnerRecurrence.selectedItemPosition]) {
            is TaskRecurrence.Daily -> TaskRecurrence.Daily(freq)
            is TaskRecurrence.Weekly -> {
                val weekdays = getSelectedWeekdays().ifEmpty {
                    listOf(ZonedDateTime.now().get(ChronoField.DAY_OF_WEEK))
                }
                TaskRecurrence.Weekly(freq, weekdays)
            }
            is TaskRecurrence.Monthly -> TaskRecurrence.Monthly(freq)
            is TaskRecurrence.Annually -> TaskRecurrence.Annually(freq)
            is TaskRecurrence.SingleTask -> TaskRecurrence.SingleTask(freq)
        }
    }

    private fun getSelectedWeekdays(): List<Int> {
        val buttonGroup = binding.taskRepeatView.recurrenceWeekPicker.weekdayButtonGroup
        return buttonGroup
            .checkedButtonIds
            .map { buttonId ->
                val btn = buttonGroup.findViewById<MaterialButton>(buttonId)
                buttonGroup.indexOfChild(btn) + 1
            } // map checked buttons to weekday index 0..6 (mo - su)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val newDate = ZonedDateTime.of(
            LocalDate.of(year, month + 1, dayOfMonth),
            task.metaData.startDateTime.toLocalTime(),
            ZoneId.systemDefault()
        )

        // set new task date
        task.metaData.startDateTime = newDate

        // update UI
        binding.taskDateInput.setText(formatDate(newDate))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // set new task time
        val newTime = ZonedDateTime.of(
            task.metaData.startDateTime.toLocalDate(),
            LocalTime.of(hourOfDay, minute),
            ZoneId.systemDefault()
        )
        // set new task date
        task.metaData.startDateTime = newTime

        // update UI
        binding.taskTimeInput.setText(formatTime(task.metaData.startDateTime))
    }

    private fun formatDate(taskDateTime: ZonedDateTime): String {
        val formattedDayOfMonth = taskDateTime.dayOfMonth.toString()
        val formattedMonth = taskDateTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val formattedYear = taskDateTime.year.toString()
        return "$formattedDayOfMonth $formattedMonth $formattedYear"
    }

    private fun formatTime(taskDateTime: ZonedDateTime): String {
        return String.format("%02d:%02d", taskDateTime.hour, taskDateTime.minute)
    }

    private fun presentError(stringRes: Int) {
        val rootView = findViewById<View>(android.R.id.content) ?: return
        rootView.snackbar(message = getString(stringRes))
    }
}
