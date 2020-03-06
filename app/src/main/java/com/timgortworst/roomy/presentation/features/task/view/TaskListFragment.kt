package com.timgortworst.roomy.presentation.features.task.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SnapshotMetadata
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.FragmentTaskListBinding
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskMetaData
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.domain.utils.NotificationWorkerBuilder
import com.timgortworst.roomy.presentation.base.view.AdapterStateListener
import com.timgortworst.roomy.presentation.base.view.ChildChangedListener
import com.timgortworst.roomy.presentation.features.task.presenter.TaskListPresenter
import com.timgortworst.roomy.presentation.features.task.recyclerview.*
import com.timgortworst.roomy.presentation.features.task.viewmodel.TaskViewModel
import com.timgortworst.roomy.presentation.features.main.MainActivity
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TaskListFragment : Fragment(),
    ActionModeCallback.ActionItemListener,
    TaskDoneClickListener,
    TaskListView,
    AdapterStateListener, ChildChangedListener {

    private lateinit var parentActivity: AppCompatActivity
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskListAdapter: TaskFirestoreAdapter
    private lateinit var notificationWorkerBuilder: NotificationWorkerBuilder
    private var tracker: SelectionTracker<String>? = null
    private var actionMode: ActionMode? = null
    private val taskViewModel by viewModel<TaskViewModel>()
    private val presenter: TaskListPresenter by inject { parametersOf(this) }

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
        createFireStoreRvAdapter()

        notificationWorkerBuilder = NotificationWorkerBuilder(parentActivity)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createFireStoreRvAdapter() = taskViewModel.fetchFireStoreRecyclerOptionsBuilder()
        .observe(viewLifecycleOwner, Observer { networkResponse ->
            networkResponse?.let {
                hideLoadingState() // todo better place
                val options = it.setLifecycleOwner(this).build()
                taskListAdapter.updateOptions(options)
            }
        })

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
                tracker?.let { presenter.onSelectionChanged(it, actionMode) }
            }
        })
    }

    override fun startActionMode(tracker: SelectionTracker<String>) {
        actionMode = parentActivity.startSupportActionMode(
            ActionModeCallback(
                this@TaskListFragment,
                tracker,
                taskListAdapter.snapshots
            )
        )
    }

    override fun stopActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    override fun invalidateActionMode() {
        actionMode?.invalidate()
    }

    override fun setActionModeTitle(size: Int) {
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
        }
    }

    override fun showToast(stringRes: Int) {
        Toast.makeText(parentActivity, getString(stringRes), Toast.LENGTH_LONG).show()
    }

    override fun onTaskDoneClicked(
        task: Task,
        position: Int
    ) {
        taskViewModel.viewModelScope.launch {
            taskViewModel.tasksCompleted(listOf(task))
        }
    }

    override fun enqueueNotification(
        taskId: String,
        taskMetaData: TaskMetaData,
        taskName: String,
        userName: String
    ) {
        notificationWorkerBuilder.enqueueNotification(
            taskId,
            taskMetaData,
            userName,
            taskName
        )
    }

    override fun removePendingNotificationReminder(taskId: String) {
        notificationWorkerBuilder.removePendingNotificationReminder(taskId)
    }

    private fun askForDeleteDialog(tasks: List<Task>, mode: ActionMode) =
        MaterialAlertDialogBuilder(parentActivity)
            .setTitle(R.string.delete)
            .setMessage(getString(R.string.delete_dialog_text, tasks.size))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.delete) { dialog, _ ->
                taskViewModel.viewModelScope.launch {
                    taskViewModel.deleteTasks(tasks)
                }
                mode.finish()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()


    private fun setMsgView(isVisible: Int, title: Int?, text: Int?) {
        binding.layoutListState.apply {
            title?.let { this.stateTitle.text = parentActivity.getString(it) }
            text?.let { this.stateMessage.text = parentActivity.getString(it) }
            root.visibility = isVisible
        }
    }

    override fun onDataChanged(itemCount: Int) {
        binding.recyclerView.visibility = View.VISIBLE
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

    private fun hideLoadingState() {
        val animationDuration = resources.getInteger(android.R.integer.config_mediumAnimTime)

        binding.recyclerView.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(animationDuration.toLong())
                .setListener(null)
        }

        binding.progress.root.animate()
            .alpha(0f)
            .setDuration(animationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.progress.root.visibility = View.GONE
                }
            })
    }

    override fun onChildChanged(
        type: ChangeEventType,
        task: Task?
    ) {
        presenter.renderDataState(type, task)
    }
}
