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
import androidx.recyclerview.widget.LinearLayoutManager
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.databinding.ActivityEditTaskBinding
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.TaskRecurrence
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.infrastructure.adapter.OpenAdapter
import com.timgortworst.tasqs.infrastructure.adapter.provider.StableIdProvider
import com.timgortworst.tasqs.infrastructure.adapter.viewholder.ViewHolderBinder
import com.timgortworst.tasqs.infrastructure.extension.snackbar
import com.timgortworst.tasqs.presentation.base.model.EventObserver
import com.timgortworst.tasqs.presentation.features.task.adapter.formatTime
import com.timgortworst.tasqs.presentation.features.task.viewholderbinder.EditTextViewHolderBinder
import com.timgortworst.tasqs.presentation.features.task.viewholderbinder.RecurrenceViewHolderBinder
import com.timgortworst.tasqs.presentation.features.task.viewholderbinder.TextViewHolderBinder
import com.timgortworst.tasqs.presentation.features.task.viewholderbinder.UserSpinnerViewHolderBinder
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
    private val adapter: OpenAdapter = OpenAdapter().apply {
        setHasStableIds(true)
        addStableIdsProvider(object: StableIdProvider {
            override fun getItemId(item: Any?, viewHolderBinder: ViewHolderBinder<*, *>?): Long? {
                return when(item) {
                    is UserSpinnerViewHolderBinder.ViewItem -> item.currentUser?.userId.hashCode().toLong()
                    null -> 999L
                    else -> item::class.java.hashCode().toLong()
                }
            }
        })
    }

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
        setupAdapter()

        updateAdapter()

        viewModel.actionDone.observe(this, EventObserver {
            binding.progressBar.visibility = View.INVISIBLE
            when (it) {
                Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> navigateUpTo(parentActivityIntent)
                is Response.Error -> presentError(R.string.error_generic)
                is Response.Empty -> run {

                    adapter.removeItem(0)
                    setupTaskDescription(task.description, getString(it.msg))
                    adapter.notifyItemChanged(0)
                }
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
    }

    private fun updateAdapter() {
        adapter.clear()

        setupTaskDescription(task.description)
        setupTaskUser(task.user)
        setupTaskDate(task.metaData.startDateTime)
        setupTaskTime(task.metaData.startDateTime)
        setupTaskRecurrence(task.metaData.recurrence)

        adapter.notifyDataSetChanged()
    }

    private fun setupTaskDescription(taskDescription: String, errorMessage: String? = null) {
        adapter.addItem(0,
            EditTextViewHolderBinder.ViewItem(
                taskDescription,
                errorMessage,
                object : EditTextViewHolderBinder.Callback {
                    override fun onDescriptionInput(text: String) {
                        task.description = text
                    }
                }), EditTextViewHolderBinder()
        )
    }

    private fun setupTaskUser(user: Task.User?) {
        val userLoadingSpinnerViewHolderBinder = UserSpinnerViewHolderBinder(this)
        userLoadingSpinnerViewHolderBinder.callback =
            object : UserSpinnerViewHolderBinder.Callback {
                override fun onSpinnerSelection(response: Response<Task.User>) {
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

        adapter.addItem(
            UserSpinnerViewHolderBinder.ViewItem(getString(R.string.edit_task_user_hint), user),
            userLoadingSpinnerViewHolderBinder
        )
    }

    private fun setupTaskDate(taskDateTime: ZonedDateTime) {
        adapter.addItem(
            TextViewHolderBinder.ViewItem(
                formatDate(taskDateTime),
                R.string.edit_task_date_hint,
                object : TextViewHolderBinder.Callback {
                    override fun onClick() {
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
                }), TextViewHolderBinder()
        )
    }

    private fun setupTaskTime(taskDateTime: ZonedDateTime) {
        adapter.addItem(
            TextViewHolderBinder.ViewItem(
                formatTime(taskDateTime),
                R.string.edit_task_time_hint,
                object : TextViewHolderBinder.Callback {
                    override fun onClick() {
                        with(task.metaData.startDateTime) {
                            TimePickerDialog(
                                this@TaskEditActivity, this@TaskEditActivity,
                                hour, minute, true
                            ).show()
                        }
                    }
                }), TextViewHolderBinder()
        )
    }

    private fun setupTaskRecurrence(taskRecurrence: TaskRecurrence) {
        val recurrenceViewHolderBinder = RecurrenceViewHolderBinder()
        recurrenceViewHolderBinder.callback = object : RecurrenceViewHolderBinder.Callback {
            override fun onRecurrenceSelection(selection: TaskRecurrence) {
                task.metaData.recurrence = selection
            }
        }

        adapter.addItem(
            RecurrenceViewHolderBinder.ViewItem(taskRecurrence),
            recurrenceViewHolderBinder
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        task.metaData.startDateTime = ZonedDateTime.of(
            LocalDate.of(year, month + 1, dayOfMonth),
            task.metaData.startDateTime.toLocalTime(),
            task.metaData.startDateTime.zone
        )

        updateAdapter()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        task.metaData.startDateTime = ZonedDateTime.of(
            task.metaData.startDateTime.toLocalDate(),
            LocalTime.of(hourOfDay, minute),
            task.metaData.startDateTime.zone
        )

        updateAdapter()
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
}
