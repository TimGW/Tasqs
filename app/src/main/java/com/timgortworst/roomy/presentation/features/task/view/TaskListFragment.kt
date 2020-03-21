package com.timgortworst.roomy.presentation.features.task.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.FragmentTaskListBinding
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.domain.utils.snackbar
import com.timgortworst.roomy.presentation.base.view.AdapterStateListener
import com.timgortworst.roomy.presentation.base.view.BaseFragment
import com.timgortworst.roomy.presentation.features.main.MainActivity
import com.timgortworst.roomy.presentation.features.task.recyclerview.*
import com.timgortworst.roomy.presentation.features.task.viewmodel.TaskListViewModel
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.*

class TaskListFragment : BaseFragment(),
    ActionModeCallback.ActionItemListener,
    TaskClickListener,
    AdapterStateListener {

    private lateinit var parentActivity: MainActivity
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskListAdapter: TaskFirestoreAdapter
    private var tracker: SelectionTracker<String>? = null
    private var actionMode: ActionMode? = null
    private val taskViewModel by viewModel<TaskListViewModel>()
    private var showListAnimation = true

    companion object {
        const val TASK_SELECTION_ID = "Task-selection"
        const val IS_IN_ACTION_MODE_KEY = "ActionMode"

        fun newInstance(): TaskListFragment {
            return TaskListFragment()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let { bundle ->
            tracker?.let {
                it.onRestoreInstanceState(bundle)
                if (bundle.getBoolean(IS_IN_ACTION_MODE_KEY, false)) startActionMode(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
        outState.putBoolean(IS_IN_ACTION_MODE_KEY, actionMode != null)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = (activity as? MainActivity) ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)

        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel.data.observe(viewLifecycleOwner, Observer { networkResponse ->
            networkResponse?.let {
                val options = it.setLifecycleOwner(this).build()
                taskListAdapter.updateOptions(options)
            }
        })

        taskViewModel.showLoading.observe(viewLifecycleOwner, Observer { networkResponse ->
            networkResponse?.let {
                if (it) {
                    toggleFadeViews(binding.recyclerView, binding.progress)
                } else {
                    toggleFadeViews(binding.progress, binding.recyclerView)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        // todo remove this placeholder options
        val query =
            FirebaseFirestore.getInstance().collection(TaskJson.TASK_COLLECTION_REF).whereEqualTo(
                TaskJson.TASK_HOUSEHOLD_ID_REF, ""
            )
        val defaultOptions = FirestoreRecyclerOptions
            .Builder<Task>()
            .setQuery(query, Task::class.java)
            .build()

        taskListAdapter = TaskFirestoreAdapter(
            this,
            this,
            defaultOptions
        )

        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(parentActivity)
            layoutManager = linearLayoutManager
            adapter = taskListAdapter
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            setupSelectionTracker(this)
        }
    }

    private fun setupSelectionTracker(recyclerView: RecyclerView) {
        tracker = SelectionTracker.Builder<String>(
            TASK_SELECTION_ID,
            recyclerView,
            TaskItemKeyProvider(recyclerView.adapter),
            TaskItemDetailsLookup(recyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).withOnDragInitiatedListener {
            true
        }.build()

        taskListAdapter.tracker = tracker

        tracker?.addObserver(object : SelectionTracker.SelectionObserver<String>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                tracker?.let { onSelectionChanged(it, actionMode) }
            }
        })
    }

    private fun onSelectionChanged(tracker: SelectionTracker<String>, actionMode: ActionMode?) {
        if (tracker.hasSelection() && actionMode == null) {
            startActionMode(tracker)
            setActionModeTitle(tracker.selection.size())
        } else if (!tracker.hasSelection() && actionMode != null) {
            stopActionMode()
        } else {
            setActionModeTitle(tracker.selection.size())
            invalidateActionMode()
        }
    }

    private fun startActionMode(tracker: SelectionTracker<String>) {
        actionMode = parentActivity.startSupportActionMode(
            ActionModeCallback(
                this@TaskListFragment,
                tracker,
                taskListAdapter.snapshots
            )
        )
    }

    private fun stopActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    private fun invalidateActionMode() {
        actionMode?.invalidate()
    }

    private fun setActionModeTitle(size: Int) {
        actionMode?.apply {
            menu?.findItem(R.id.edit)?.isVisible = size == 1
            menu?.findItem(R.id.info)?.isVisible = size == 1
            title = size.toString()
        }
    }

    override fun onActionItemDelete(selectedTasks: List<Task>, mode: ActionMode) {
        askForDeleteDialog(selectedTasks, mode).show()
    }

    override fun onActionItemEdit(selectedTask: Task) {
        TaskEditActivity.start(parentActivity, selectedTask)
    }

    override fun onActionItemInfo(selectedTask: Task) {
        TaskInfoActivity.start(parentActivity, selectedTask)
    }

    override fun onActionItemDone(selectedTasks: List<Task>) {
        taskViewModel.viewModelScope.launch {
            taskViewModel.tasksCompleted(selectedTasks)

            parentActivity.binding.bottomNavigationContainer.snackbar(
                message = getString(R.string.tasks_done, selectedTasks.size),
                anchorView = parentActivity.binding.fab
            )
        }
    }

    override fun onTaskDoneClicked(
        task: Task,
        position: Int
    ) {
        taskViewModel.viewModelScope.launch {
            taskViewModel.tasksCompleted(listOf(task))

            if (task.metaData.recurrence !is TaskRecurrence.SingleTask) {
                parentActivity.binding.bottomNavigationContainer.snackbar(
                    message = getString(
                        R.string.task_next_snackbar,
                        task.description,
                        formatDate(task.metaData.startDateTime)
                    ),
                    anchorView = parentActivity.binding.fab
                )
            }
        }
    }

    override fun onTaskInfoClicked(task: Task) {
        TaskInfoActivity.start(parentActivity, task)
    }

    private fun formatDate(zonedDateTime: ZonedDateTime): String {
        val formattedDayOfMonth = zonedDateTime.dayOfMonth.toString()
        val formattedMonth = zonedDateTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val formattedYear = zonedDateTime.year.toString()
        return "$formattedDayOfMonth $formattedMonth $formattedYear"
    }

    private fun askForDeleteDialog(tasks: List<Task>, mode: ActionMode) =
        MaterialAlertDialogBuilder(parentActivity)
            .setTitle(R.string.delete)
            .setMessage(getString(R.string.delete_dialog_text, tasks.size))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.delete) { dialog, _ ->
                taskViewModel.viewModelScope.launch {
                    taskViewModel.deleteTasks(tasks)

                    if (tasks.size == 1) {
                        parentActivity.binding.bottomNavigationContainer.snackbar(
                            message = getString(R.string.task_deleted, tasks.first().description),
                            anchorView = parentActivity.binding.fab
                        )
                    } else {
                        parentActivity.binding.bottomNavigationContainer.snackbar(
                            message = getString(R.string.tasks_deleted, tasks.size),
                            anchorView = parentActivity.binding.fab
                        )
                    }
                }
                mode.finish()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()


    private fun setMsgView(isVisible: Int, title: Int?, text: Int?) {
        binding.layoutStateMessage.apply {
            title?.let { this.stateTitle.text = parentActivity.getString(it) }
            text?.let { this.stateMessage.text = parentActivity.getString(it) }
            root.visibility = isVisible
        }
    }

    override fun onDataChanged(itemCount: Int) {
        binding.recyclerView.visibility = View.VISIBLE
        if (showListAnimation) binding.recyclerView.scheduleLayoutAnimation(); showListAnimation = false
        val visibility = if (itemCount == 0) View.VISIBLE else View.GONE
        setMsgView(
            visibility,
            R.string.empty_list_state_title_tasks,
            R.string.empty_list_state_text_tasks
        )
    }

    override fun onError(e: FirebaseFirestoreException) {
        binding.recyclerView.visibility = View.GONE
        setMsgView(
            View.VISIBLE,
            R.string.error_list_state_title,
            R.string.error_list_state_text
        )
    }
}
