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
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.ActivityEditTaskBinding
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.domain.model.TaskUser
import com.timgortworst.roomy.domain.utils.clearFocus
import com.timgortworst.roomy.domain.utils.snackbar
import com.timgortworst.roomy.presentation.base.model.EventObserver
import com.timgortworst.roomy.presentation.features.main.MainActivity
import com.timgortworst.roomy.presentation.features.task.viewmodel.TaskEditViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.threeten.bp.*
import org.threeten.bp.temporal.ChronoField

// todo refactor logic to viewmodel / usecase
class TaskEditActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: ActivityEditTaskBinding
    private val viewModel: TaskEditViewModel by inject()
    private var userList: List<TaskUser> = listOf()
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

        fun intentBuilder(context: AppCompatActivity, task: Task? = null): Intent {
            val intent = Intent(context, TaskEditActivity::class.java)
            task?.let { intent.putExtra(INTENT_EXTRA_EDIT_TASK, it) }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        task = intent.getParcelableExtra(INTENT_EXTRA_EDIT_TASK) as? Task
            ?: Task()

        setupUI()

        viewModel.prettyDate.observe(this, EventObserver { binding.taskDateInput.setText(it) })

        viewModel.taskDone.observe(this, EventObserver {
            binding.progressBar.visibility = View.INVISIBLE
            when (it) {
                Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> navigateUpTo(Intent(this, MainActivity::class.java))
                is Response.Error -> presentError(R.string.error_generic)
                is Response.Empty -> binding.taskDescriptionHint.error = getString(it.msg)
            }
        })

        viewModel.allUsersLiveData.observe(this, Observer { response ->
            binding.progressBar.visibility = View.INVISIBLE
            when (response) {
                Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> {
                    viewModel.viewModelScope.launch {
                        val currentUser = response.data?.find {
                            it.userId == FirebaseAuth.getInstance().currentUser?.uid
                        } ?: return@launch

                        if (response.data.filterNot { it.userId == currentUser.userId }.isEmpty()) {
                            presentCurrentUser(
                                TaskUser(
                                    currentUser.userId,
                                    currentUser.name
                                )
                            )
                        } else {
                            presentUserList(response.data.map {
                                TaskUser(
                                    it.userId,
                                    it.name
                                )
                            })
                        }

                    }
                }
                is Response.Error -> presentError(R.string.users_loading_error)
            }
        })
    }

    private fun setupUI() {
        setupToolbar()
        setupListeners()

        recurrenceAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            recurrences.map { getString(it.name) }
        )
        binding.taskRepeatView.spinnerRecurrence.adapter = recurrenceAdapter

        viewModel.formatDate(task.metaData.startDateTime)

        if (intent.hasExtra(INTENT_EXTRA_EDIT_TASK) &&
            intent.getParcelableExtra(INTENT_EXTRA_EDIT_TASK) as? Task != null
        ) {
            setupEditUI()
        }
    }

    private fun setupEditUI() {
        supportActionBar?.title = getString(R.string.toolbar_title_edit_task, task.description)
        binding.taskDescription.setText(task.description)
        viewModel.formatDate(task.metaData.startDateTime)

        val isRepeating = task.metaData.recurrence !is TaskRecurrence.SingleTask
        binding.taskRepeatCheckbox.isChecked = isRepeating

        val index = recurrences.indexOfFirst { it.name == task.metaData.recurrence.name }
        binding.taskRepeatView.spinnerRecurrence.setSelection(index)
        binding.taskRepeatView.root.visibility = if (isRepeating) View.VISIBLE else View.GONE
        val freq = task.metaData.recurrence.frequency.toString()
        binding.taskRepeatView.recurrenceFrequency.setText(freq)
        (task.metaData.recurrence as? TaskRecurrence.Weekly)?.let { weekly ->
            val recurrenceWeekPicker = binding.taskRepeatView.recurrenceWeekPicker
            recurrenceWeekPicker.root.visibility = View.VISIBLE
            val weekdayButtonGroup = recurrenceWeekPicker.weekdayButtonGroup
            weekly.onDaysOfWeek.forEach { index ->
                weekdayButtonGroup.check(weekdayButtonGroup[index - 1].id)
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
        binding.taskDescription.doAfterTextChanged {
            if (it?.isNotBlank() == true) binding.taskDescriptionHint.error = null
            task.description = binding.taskDescription.text.toString()
        }

        binding.taskDateInput.setOnClickListener {
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

        binding.taskRepeatCheckbox.setOnCheckedChangeListener { _, isChecked ->
            clearAllFocus()

            binding.taskRepeatView.root.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.taskRepeatView.recurrenceFrequency.setOnFocusChangeListener { v, hasFocus ->
            binding.taskRepeatView.recurrenceFrequency.apply {
                if (text.toString().isBlank() && !hasFocus) {
                    setText("1")
                }
            }
        }

        binding.taskRepeatView.recurrenceFrequency.doAfterTextChanged {
            it?.let {
                val input = it.toString()
                if (input.isNotEmpty() && input.first() == '0') {
                    it.replace(0, 1, "1")
                }
            }

            val input = binding.taskRepeatView.recurrenceFrequency.text.toString()

            if (input.toIntOrNull()?.equals(1) == true || input.isBlank()) {
                setSingularSpinner()
            } else {
                setPluralSpinner()
            }
        }

        binding.taskRepeatView.spinnerRecurrence.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    clearAllFocus()
                    binding.taskRepeatView.recurrenceWeekPicker.root.visibility =
                        if (recurrenceFromSelection() is TaskRecurrence.Weekly) View.VISIBLE else View.GONE
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        binding.taskRepeatView.recurrenceWeekPicker.weekdayButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            clearAllFocus()
        }
    }

    fun clearAllFocus() {
        clearFocus(binding.taskRepeatView.recurrenceFrequency)
        clearFocus(binding.taskDescription)
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
                val result = binding.spinnerUsers.selectedItemPosition
                if (result != -1) task.user = userList[result]
                task.metaData.recurrence = recurrenceFromSelection()
                viewModel.taskDoneClicked(task)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun recurrenceFromSelection(): TaskRecurrence {
        if (!binding.taskRepeatCheckbox.isChecked) return TaskRecurrence.SingleTask()
        val freq = binding.taskRepeatView.recurrenceFrequency.text.toString().toIntOrNull() ?: 1

        return when (recurrences[binding.taskRepeatView.spinnerRecurrence.selectedItemPosition]) {
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
        val buttonGroup = binding.taskRepeatView.recurrenceWeekPicker.weekdayButtonGroup
        return buttonGroup
            .checkedButtonIds
            .map { buttonId ->
                val btn = buttonGroup.findViewById<MaterialButton>(buttonId)
                buttonGroup.indexOfChild(btn) + 1
            } // map checked buttons to weekday index 0..6 (mo - su)
    }

    private fun presentUserList(filteredUserList: List<TaskUser>) {
        this.userList = filteredUserList

        binding.userGroup.visibility = View.VISIBLE

        val userAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            filteredUserList.map { it.name }
        )
        binding.spinnerUsers.adapter = userAdapter

        if (intent.hasExtra(INTENT_EXTRA_EDIT_TASK)) {
            val index = filteredUserList.indexOf(task.user)
            binding.spinnerUsers.setSelection(index)
        }
    }

    private fun presentCurrentUser(currentUser: TaskUser?) {
        currentUser?.let { task.user = it }
    }

    private fun setPluralSpinner() {
        recurrenceAdapter.clear()
        recurrenceAdapter.addAll(recurrences.map { getString(it.pluralName) })
        recurrenceAdapter.notifyDataSetChanged()
    }

    private fun setSingularSpinner() {
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
        viewModel.formatDate(task.metaData.startDateTime)
    }

    private fun presentError(stringRes: Int) {
        val rootView = findViewById<View>(android.R.id.content) ?: return
        rootView.snackbar(message = getString(stringRes))
    }
}
