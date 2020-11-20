package com.timgortworst.tasqs.presentation.features.task.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.databinding.ActivityEditTaskBinding
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.TaskRecurrence
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.infrastructure.extension.clearFocus
import com.timgortworst.tasqs.infrastructure.extension.snackbar
import com.timgortworst.tasqs.presentation.base.model.EventObserver
import com.timgortworst.tasqs.presentation.features.task.adapter.formatTime
import com.timgortworst.tasqs.presentation.features.task.adapter.EditTextAdapter
import com.timgortworst.tasqs.presentation.features.task.adapter.RecurrenceAdapter
import com.timgortworst.tasqs.presentation.features.task.adapter.TextViewAdapter
import com.timgortworst.tasqs.presentation.features.task.adapter.UserSpinnerAdapter
import com.timgortworst.tasqs.presentation.features.task.viewmodel.TaskEditViewModel
import org.koin.android.ext.android.inject
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.*

class TaskEditActivity : AppCompatActivity(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val task: Task by lazy {
        intent.getParcelableExtra(INTENT_EXTRA_EDIT_TASK) as? Task ?: Task()
    }
    private lateinit var binding: ActivityEditTaskBinding
    private val isEditMode: Boolean by lazy { intent.hasExtra(INTENT_EXTRA_EDIT_TASK) }
    private val viewModel: TaskEditViewModel by inject()

    private val adapter = ConcatAdapter()
    private lateinit var recurrenceAdapter: RecurrenceAdapter
    private lateinit var timeAdapter: TextViewAdapter
    private lateinit var dateAdapter: TextViewAdapter
    private lateinit var userAdapter: UserSpinnerAdapter
    private lateinit var descriptionAdapter: EditTextAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(task.description)
        setupAdapter()

        viewModel.actionDone.observe(this, EventObserver {
            binding.progressBar.visibility = View.INVISIBLE
            when (it) {
                Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> navigateUpTo(parentActivityIntent)
                is Response.Error -> presentError(R.string.error_generic)
                is Response.Empty -> { }
            }
        })

        viewModel.emptyUserMsg.observe(this, EventObserver{ presentError(it) })

        viewModel.emptyDescMsg.observe(this, EventObserver{
            descriptionAdapter.setViewItem(buildDescriptionViewItem(task.description, getString(it)))
            adapter.notifyItemChanged(TASK_DESC_POSITION)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_done -> {
                viewModel.taskDoneClicked(task)
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

    private fun setupAdapter() {
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)

        descriptionAdapter = EditTextAdapter(buildDescriptionViewItem(task.description))
        userAdapter = UserSpinnerAdapter(this, buildUserViewItem(task.user))
        dateAdapter = TextViewAdapter(buildDateViewItem(task.metaData.startDateTime))
        timeAdapter = TextViewAdapter(buildTimeViewItem(task.metaData.startDateTime))
        recurrenceAdapter = RecurrenceAdapter(buildRecurrenceViewItem(task.metaData.recurrence))

        adapter.addAdapter(TASK_DESC_POSITION, descriptionAdapter)
        adapter.addAdapter(TASK_USER_POSITION, userAdapter)
        adapter.addAdapter(TASK_DATE_POSITION, dateAdapter)
        adapter.addAdapter(TASK_TIME_POSITION, timeAdapter)
        adapter.addAdapter(TASK_REC_POSITION, recurrenceAdapter)
        adapter.notifyItemRangeInserted(0, adapter.itemCount)
    }

    private fun buildDescriptionViewItem(
        taskDescription: String,
        errorMessage: String? = null
    ): EditTextAdapter.ViewItem {
        return EditTextAdapter.ViewItem(
            taskDescription,
            errorMessage,
            object : EditTextAdapter.Callback {
                override fun onDescriptionInput(text: String) {
                    task.description = text
                }
            })
    }

    private fun buildUserViewItem(
        user: Task.User?
    ): UserSpinnerAdapter.ViewItem {
        val viewItem = UserSpinnerAdapter.ViewItem(getString(R.string.edit_task_user_hint), user)
        viewItem.callback = object : UserSpinnerAdapter.Callback {
            override fun onSpinnerSelection(response: Response<Task.User>) {
                clearFocus(binding.recyclerView)
                binding.progressBar.visibility = View.INVISIBLE

                when (response) {
                    Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Response.Error -> presentError(R.string.users_loading_error)
                    is Response.Success -> {
                        val data = response.data ?: return
                        task.user = data
                    }
                }
            }
        }
        return viewItem
    }

    private fun buildDateViewItem(
        taskDateTime: ZonedDateTime
    ): TextViewAdapter.ViewItem {
        val viewItem = TextViewAdapter.ViewItem(formatDate(taskDateTime), R.string.edit_task_date_hint)
        viewItem.callback = object : TextViewAdapter.Callback {
            override fun onClick() {
                clearFocus(binding.recyclerView)
                with(task.metaData.startDateTime) {
                    DatePickerDialog(
                        this@TaskEditActivity, this@TaskEditActivity,
                        year, monthValue - 1, dayOfMonth
                    ).apply {
                        datePicker.minDate =
                            ZonedDateTime.now().plusDays(1).toInstant().toEpochMilli()
                        show()
                    }
                }
            }
        }

        return viewItem
    }

    private fun buildTimeViewItem(
        taskDateTime: ZonedDateTime
    ): TextViewAdapter.ViewItem {
        val viewItem = TextViewAdapter.ViewItem(formatTime(taskDateTime), R.string.edit_task_time_hint)
        viewItem.callback = object : TextViewAdapter.Callback {
            override fun onClick() {
                clearFocus(binding.recyclerView)
                with(task.metaData.startDateTime) {
                    TimePickerDialog(
                        this@TaskEditActivity, this@TaskEditActivity,
                        hour, minute, true
                    ).show()
                }
            }
        }
        return viewItem
    }

    private fun buildRecurrenceViewItem(
        taskRecurrence: TaskRecurrence
    ): RecurrenceAdapter.ViewItem {
        val viewItem = RecurrenceAdapter.ViewItem(taskRecurrence)
        viewItem.callback = object : RecurrenceAdapter.Callback {
            override fun onRecurrenceSelection(selection: TaskRecurrence) {
                clearFocus(binding.recyclerView)
                task.metaData.recurrence = selection
            }
        }

        return viewItem
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        task.metaData.startDateTime = ZonedDateTime.of(
            LocalDate.of(year, month + 1, dayOfMonth),
            task.metaData.startDateTime.toLocalTime(),
            task.metaData.startDateTime.zone
        )

        dateAdapter.setViewItem(buildDateViewItem(task.metaData.startDateTime))
        adapter.notifyItemChanged(TASK_DATE_POSITION)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        task.metaData.startDateTime = ZonedDateTime.of(
            task.metaData.startDateTime.toLocalDate(),
            LocalTime.of(hourOfDay, minute),
            task.metaData.startDateTime.zone
        )

        timeAdapter.setViewItem(buildTimeViewItem(task.metaData.startDateTime))
        adapter.notifyItemChanged(TASK_TIME_POSITION)
    }

    private fun formatDate(taskDateTime: ZonedDateTime): String {
        val formattedDayOfMonth = taskDateTime.dayOfMonth.toString()
        val formattedMonth = taskDateTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val formattedYear = taskDateTime.year.toString()
        return "$formattedDayOfMonth $formattedMonth $formattedYear"
    }

    private fun presentError(stringRes: Int) {
        val rootView = findViewById<View>(android.R.id.content) ?: return
        rootView.snackbar(message = getString(stringRes))
    }

    companion object {
        const val INTENT_EXTRA_EDIT_TASK = "INTENT_EXTRA_EDIT_TASK"

        const val TASK_DESC_POSITION = 0
        const val TASK_USER_POSITION = 1
        const val TASK_DATE_POSITION = 2
        const val TASK_TIME_POSITION = 3
        const val TASK_REC_POSITION = 4

        fun intentBuilder(context: Context, task: Task? = null): Intent {
            val intent = Intent(context, TaskEditActivity::class.java)
            task?.let { intent.putExtra(INTENT_EXTRA_EDIT_TASK, it) }
            return intent
        }
    }
}
