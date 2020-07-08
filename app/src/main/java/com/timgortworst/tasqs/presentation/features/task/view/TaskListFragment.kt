package com.timgortworst.tasqs.presentation.features.task.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.databinding.FragmentTaskListBinding
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.infrastructure.extension.snackbar
import com.timgortworst.tasqs.presentation.base.view.AdapterStateListener
import com.timgortworst.tasqs.presentation.base.view.ViewFadeAnimator.toggleFadeViews
import com.timgortworst.tasqs.presentation.features.main.MainActivity
import com.timgortworst.tasqs.presentation.features.task.adapter.TaskClickListener
import com.timgortworst.tasqs.presentation.features.task.adapter.TaskFirestoreAdapter
import com.timgortworst.tasqs.presentation.features.task.actionmode.ActionModeCallback
import com.timgortworst.tasqs.presentation.features.task.actionmode.TaskItemDetailsLookup
import com.timgortworst.tasqs.presentation.features.task.actionmode.TaskItemKeyProvider
import com.timgortworst.tasqs.presentation.features.task.viewmodel.TaskListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class TaskListFragment : Fragment(),
    ActionModeCallback.ActionItemListener,
    TaskClickListener,
    AdapterStateListener {
    private var _binding: FragmentTaskListBinding? = null
    private var taskListAdapter: TaskFirestoreAdapter? = null
    private var tracker: SelectionTracker<String>? = null
    private var actionMode: ActionMode? = null
    private var showListAnimation = true
    private val taskViewModel by viewModel<TaskListViewModel>()
    private val binding get() = _binding!!

    companion object {
        private const val TASK_SELECTION_ID = "Task-selection"
        private const val IS_IN_ACTION_MODE_KEY = "ActionMode"

        fun newInstance(): TaskListFragment {
            return TaskListFragment()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
        outState.putBoolean(IS_IN_ACTION_MODE_KEY, actionMode != null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)

        taskViewModel.viewModelScope.launch {
            taskViewModel.loadInitialQuery()
            setupAdapter()
            setupRecyclerView()

            savedInstanceState?.let { bundle ->
                tracker?.let {
                    it.onRestoreInstanceState(bundle)
                    if (bundle.getBoolean(IS_IN_ACTION_MODE_KEY, false)) startActionMode(it)
                    setActionModeTitle(it.selection.size())
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel.showLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading == true) {
                toggleFadeViews(binding.recyclerView, binding.progress)
            } else {
                toggleFadeViews(binding.progress, binding.recyclerView)
            }
        })

        taskViewModel.liveQueryOptions.observe(viewLifecycleOwner, Observer {
            val options = it.setLifecycleOwner(this).build()
            taskListAdapter?.stopListening()
            taskListAdapter?.updateOptions(options)
            taskListAdapter?.startListening()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter_all -> {
                taskViewModel.viewModelScope.launch { taskViewModel.allDataQuery() }
                true
            }
            R.id.filter_me -> {
                taskViewModel.viewModelScope.launch { taskViewModel.filterDataQuery() }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupAdapter() {
        taskViewModel.liveQueryOptions.value?.setLifecycleOwner(this)?.build()?.let {
            taskListAdapter = TaskFirestoreAdapter(
                this,
                this,
                it
            )
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            val activity = activity ?: return
            val linearLayoutManager = LinearLayoutManager(activity)
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
            TaskItemKeyProvider(
                recyclerView.adapter
            ),
            TaskItemDetailsLookup(
                recyclerView
            ),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).withOnDragInitiatedListener {
            true
        }.build()

        taskListAdapter?.tracker = tracker

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
        val activity = (activity as? MainActivity) ?: return

        actionMode = activity.startSupportActionMode(
            ActionModeCallback(
                this@TaskListFragment,
                tracker,
                taskListAdapter?.snapshots!!
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
        askForDeleteDialog(selectedTasks, mode)?.show()
    }

    override fun onActionItemEdit(selectedTask: Task) {
        val activity = activity ?: return
        startActivity(TaskEditActivity.intentBuilder(activity, selectedTask))
    }

    override fun onActionItemInfo(selectedTask: Task) {
        val activity = activity ?: return
        startActivity(TaskInfoActivity.intentBuilder(activity, selectedTask))
    }

    override fun onActionItemDone(selectedTasks: List<Task>) {
        taskViewModel.viewModelScope.launch {
            taskViewModel.tasksCompleted(selectedTasks).collect()

            val activity = (activity as? MainActivity) ?: return@launch
            activity.binding.bottomNavigationContainer.snackbar(
                message = getString(R.string.tasks_done, selectedTasks.size),
                anchorView = activity.binding.fab
            )
        }
    }

    override fun onTaskInfoClicked(task: Task) {
        val activity = activity ?: return
        startActivity(TaskInfoActivity.intentBuilder(activity, task))
    }

    private fun askForDeleteDialog(tasks: List<Task>, mode: ActionMode): AlertDialog? {
        val activity = (activity as? MainActivity) ?: return null

        return MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.delete)
            .setMessage(getString(R.string.delete_dialog_text, tasks.size))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.delete) { dialog, _ ->
                taskViewModel.viewModelScope.launch {
                    taskViewModel.deleteTasks(tasks).collect()
                    if (tasks.size == 1) {
                        activity.binding.bottomNavigationContainer.snackbar(
                            message = getString(R.string.task_deleted, tasks.first().description),
                            anchorView = activity.binding.fab
                        )
                    } else {
                        activity.binding.bottomNavigationContainer.snackbar(
                            message = getString(R.string.tasks_deleted, tasks.size),
                            anchorView = activity.binding.fab
                        )
                    }
                }
                mode.finish()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
    }

    private fun setMsgView(isVisible: Int, title: Int?, text: Int?) {
        binding.layoutStateMessage.apply {
            title?.let { this.stateTitle.text = getString(it) }
            text?.let { this.stateMessage.text = getString(it) }
            root.visibility = isVisible
        }
    }

    override fun onDataChanged(itemCount: Int) {
        binding.recyclerView.visibility = View.VISIBLE

        if (showListAnimation) {
            binding.recyclerView.scheduleLayoutAnimation()
            showListAnimation = false
        }

        setMsgView(
            if (itemCount == 0) View.VISIBLE else View.GONE,
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
