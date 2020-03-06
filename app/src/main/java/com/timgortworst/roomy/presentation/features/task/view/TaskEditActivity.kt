package com.timgortworst.roomy.presentation.features.task.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialContainerTransformSharedElementCallback
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.ActivityEditTaskBinding
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.utils.clearFocus
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.main.MainActivity
import com.timgortworst.roomy.presentation.features.task.presenter.TaskEditPresenter
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.threeten.bp.*
import org.threeten.bp.temporal.ChronoField

class TaskEditActivity : BaseActivity(), TaskEditView, DatePickerDialog.OnDateSetListener {
    private lateinit var binding: ActivityEditTaskBinding
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
        initAnimation()
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        task = intent.getParcelableExtra(INTENT_EXTRA_EDIT_TASK) as? Task ?: Task()

        setupUI()
    }

    private fun initAnimation() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        findViewById<View>(android.R.id.content).transitionName = "shared_element_container"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())

        val transform = MaterialContainerTransform(this).apply {
            addTarget(android.R.id.content)
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        }

        window.sharedElementEnterTransition = transform
        window.sharedElementReturnTransition = transform
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
        binding.taskRepeatView.spinnerRecurrence.adapter = recurrenceAdapter

        presenter.formatDate(task.metaData.startDateTime)

        if (intent.hasExtra(INTENT_EXTRA_EDIT_TASK) &&
            intent.getParcelableExtra(INTENT_EXTRA_EDIT_TASK) as? Task != null
        ) {
            setupEditUI()
        }
    }

    private fun setupEditUI() {
        supportActionBar?.title = getString(R.string.toolbar_title_edit_task, task.description)
        binding.taskDescription.setText(task.description)
        presenter.formatDate(task.metaData.startDateTime)

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
            presenter.disableEmptyInput(binding.taskRepeatView.recurrenceFrequency, hasFocus)
        }

        binding.taskRepeatView.recurrenceFrequency.doAfterTextChanged {
            presenter.disableInputZero(it)
            presenter.checkForPluralRecurrenceSpinner(binding.taskRepeatView.recurrenceFrequency.text.toString())
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

        binding.taskRepeatView.recurrenceWeekPicker.weekdayButtonGroup.addOnButtonCheckedListener {
                group, checkedId, isChecked ->
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
                presenter.editTaskDone(task)
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

    override fun presentUserList(filteredUserList: List<User>) {
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
        binding.taskDateInput.setText("$formattedDayOfMonth $formattedMonth $formattedYear")
    }

    override fun presentEmptyDescriptionError(errorMessage: Int) {
        binding.taskDescriptionHint.error = getString(errorMessage)
    }

    override fun finishActivity() {
        navigateUpTo(Intent(this, MainActivity::class.java))
    }
}
