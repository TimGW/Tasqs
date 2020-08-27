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
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.databinding.ActivityEditTaskBinding
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.TaskRecurrence
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.infrastructure.adapter.GenericRvAdapter
import com.timgortworst.tasqs.infrastructure.adapter.StableIdProvider
import com.timgortworst.tasqs.infrastructure.adapter.ViewHolderBinder
import com.timgortworst.tasqs.infrastructure.extension.clearFocus
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
    private val adapter = GenericRvAdapter().apply {
        setHasStableIds(true)
        addStableIdsProvider(object : StableIdProvider {
            override fun getItemId(item: Any?, viewHolderBinder: ViewHolderBinder<*, *>?): Long? {
                return when (item) {
                    is UserSpinnerViewHolderBinder.ViewItem -> item.currentUser?.userId.hashCode()
                        .toLong()
                    is TextViewHolderBinder.ViewItem -> item.hint.hashCode()
                        .toLong() + item.text.hashCode().toLong()
                    null -> 999L
                    else -> item::class.java.hashCode().toLong()
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(task.description)
        setupAdapter()
        addItemsToAdapter()

        viewModel.actionDone.observe(this, EventObserver {
            binding.progressBar.visibility = View.INVISIBLE
            when (it) {
                Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> navigateUpTo(parentActivityIntent)
                is Response.Error -> presentError(R.string.error_generic)
            }
        })

        viewModel.emptyUserMsg.observe(this, EventObserver{ presentError(it) })

        viewModel.emptyDescMsg.observe(this, EventObserver{
            val descriptionVHB = buildDescriptionVHB(task.description, getString(it))
            adapter.updateItem(TASK_DESC_POSITION, descriptionVHB.first, descriptionVHB.second)
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
        binding.recyclerView.itemAnimator = null
    }

    private fun addItemsToAdapter() {
        val descriptionVHB = buildDescriptionVHB(task.description)
        val userVHB = buildUserVHB(task.user)
        val dateVHB = buildDateViewHolderBinder(task.metaData.startDateTime)
        val timeVHB = buildTimeViewHolderBinder(task.metaData.startDateTime)
        val recurrenceVHB = buildRecurrenceViewHolderBinder(task.metaData.recurrence)

        adapter.addItem(TASK_DESC_POSITION, descriptionVHB.first, descriptionVHB.second)
        adapter.addItem(TASK_USER_POSITION, userVHB.first, userVHB.second)
        adapter.addItem(TASK_DATE_POSITION, dateVHB.first, dateVHB.second)
        adapter.addItem(TASK_TIME_POSITION, timeVHB.first, timeVHB.second)
        adapter.addItem(TASK_REC_POSITION, recurrenceVHB.first, recurrenceVHB.second)

        adapter.notifyDataSetChanged()
    }

    private fun buildDescriptionVHB(
        taskDescription: String,
        errorMessage: String? = null
    ): Pair<EditTextViewHolderBinder.ViewItem, EditTextViewHolderBinder> {
        val viewItem = EditTextViewHolderBinder.ViewItem(
            taskDescription,
            errorMessage,
            object : EditTextViewHolderBinder.Callback {
                override fun onDescriptionInput(text: String) {
                    task.description = text
                }
            })

        return Pair(viewItem, EditTextViewHolderBinder())
    }

    private fun buildUserVHB(
        user: Task.User?
    ): Pair<UserSpinnerViewHolderBinder.ViewItem, UserSpinnerViewHolderBinder> {
        val viewItem = UserSpinnerViewHolderBinder.ViewItem(getString(R.string.edit_task_user_hint), user)
        viewItem.callback = object : UserSpinnerViewHolderBinder.Callback {
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
        return Pair(viewItem, UserSpinnerViewHolderBinder(this))
    }

    private fun buildDateViewHolderBinder(
        taskDateTime: ZonedDateTime
    ): Pair<TextViewHolderBinder.ViewItem, TextViewHolderBinder> {
        val viewItem =
            TextViewHolderBinder.ViewItem(formatDate(taskDateTime), R.string.edit_task_date_hint)
        viewItem.callback = object : TextViewHolderBinder.Callback {
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

        return Pair(viewItem, TextViewHolderBinder())
    }

    private fun buildTimeViewHolderBinder(
        taskDateTime: ZonedDateTime
    ): Pair<TextViewHolderBinder.ViewItem, TextViewHolderBinder> {
        val viewItem =
            TextViewHolderBinder.ViewItem(formatTime(taskDateTime), R.string.edit_task_time_hint)
        viewItem.callback = object : TextViewHolderBinder.Callback {
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
        return Pair(viewItem, TextViewHolderBinder())
    }

    private fun buildRecurrenceViewHolderBinder(
        taskRecurrence: TaskRecurrence
    ): Pair<RecurrenceViewHolderBinder.ViewItem, RecurrenceViewHolderBinder> {
        val viewItem = RecurrenceViewHolderBinder.ViewItem(taskRecurrence)
        viewItem.callback = object : RecurrenceViewHolderBinder.Callback {
            override fun onRecurrenceSelection(selection: TaskRecurrence) {
                clearFocus(binding.recyclerView)
                task.metaData.recurrence = selection
            }
        }

        return Pair(viewItem, RecurrenceViewHolderBinder())
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        task.metaData.startDateTime = ZonedDateTime.of(
            LocalDate.of(year, month + 1, dayOfMonth),
            task.metaData.startDateTime.toLocalTime(),
            task.metaData.startDateTime.zone
        )

        val vhb = buildDateViewHolderBinder(task.metaData.startDateTime)
        adapter.updateItem(TASK_DATE_POSITION, vhb.first, vhb.second)
        adapter.notifyItemChanged(TASK_DATE_POSITION)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        task.metaData.startDateTime = ZonedDateTime.of(
            task.metaData.startDateTime.toLocalDate(),
            LocalTime.of(hourOfDay, minute),
            task.metaData.startDateTime.zone
        )

        val vhb = buildTimeViewHolderBinder(task.metaData.startDateTime)
        adapter.updateItem(TASK_TIME_POSITION, vhb.first, vhb.second)
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
