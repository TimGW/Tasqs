package com.timgortworst.tasqs.presentation.features.task.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.databinding.ActivityInfoTaskBinding
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.infrastructure.extension.snackbar
import com.timgortworst.tasqs.presentation.base.model.EventObserver
import com.timgortworst.tasqs.presentation.base.model.TaskInfoAction
import com.timgortworst.tasqs.presentation.features.main.MainActivity
import com.timgortworst.tasqs.presentation.features.task.viewmodel.TaskInfoViewModel
import kotlinx.android.synthetic.main.activity_info_task.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class TaskInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoTaskBinding
    private val taskViewModel by viewModel<TaskInfoViewModel>()

    companion object {
        private const val INTENT_EXTRA_INFO_TASK = "INTENT_EXTRA_INFO_TASK"
        private const val INTENT_EXTRA_INFO_TASK_ID = "INTENT_EXTRA_INFO_TASK_ID"

        fun intentBuilder(context: Context, task: Task): Intent {
            val intent = Intent(context, TaskInfoActivity::class.java)
            intent.putExtra(INTENT_EXTRA_INFO_TASK, task)
            return intent
        }

        fun intentBuilder(context: Context, taskId: String): Intent {
            val intent = Intent(context, TaskInfoActivity::class.java)
            intent.putExtra(INTENT_EXTRA_INFO_TASK_ID, taskId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when {
            intent.hasExtra(INTENT_EXTRA_INFO_TASK) -> {
                taskViewModel.setTaskFromLocalSource(
                    intent.getParcelableExtra<Task>(
                        INTENT_EXTRA_INFO_TASK
                    ) as Task
                )
            }
            intent.hasExtra(INTENT_EXTRA_INFO_TASK_ID) -> {
                val id = intent.getStringExtra(INTENT_EXTRA_INFO_TASK_ID)
                if (id.isNullOrBlank()) finish() else taskViewModel.fetchTask(id)
            }
            else -> finish()
        }

        with(binding) {
            viewmodel = taskViewModel
            lifecycleOwner = this@TaskInfoActivity
        }

        supportActionBar?.apply {
            title = getString(R.string.toolbar_title_info_task)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        taskViewModel.taskInfoAction.observe(this, Observer {
            progress_bar.visibility = View.GONE
            when (it) {
                TaskInfoAction.Finish -> {
                    finishAffinity()
                    startActivity(MainActivity.intentBuilder(this))
                }
                TaskInfoAction.Loading -> {
                    progress_bar.visibility = View.VISIBLE
                }
            }
        })

        taskViewModel.task.observe(this, Observer {
            val task = (it as? Response.Success)?.data ?: return@Observer

            task_done.visibility = if (isOwnTask(task)) View.VISIBLE else View.GONE

            task_done.setOnClickListener {
                taskViewModel.taskCompleted(task)
            }

            invalidateOptionsMenu()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_go_to_edit -> {
                startActivity(TaskEditActivity.intentBuilder(this, taskViewModel.getTaskOrNull()))
                true
            }
            R.id.action_delete -> {
                askForDeleteDialog().show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.info_menu, menu)

        val task = taskViewModel.getTaskOrNull() ?: return true

        menu.findItem(R.id.action_delete).isVisible = isOwnTask(task)
        menu.findItem(R.id.action_go_to_edit).isVisible = isOwnTask(task)

        return true
    }

    private fun isOwnTask(
        task: Task
    ) = task.user?.userId.equals(FirebaseAuth.getInstance().currentUser?.uid)

    private fun askForDeleteDialog(): AlertDialog {
        return MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete)
            .setMessage(getString(R.string.delete_dialog_text_single))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.delete) { dialog, _ ->
                taskViewModel.viewModelScope.launch {
                    taskViewModel.deleteTask()

                    binding.root.snackbar(
                        message = getString(
                            R.string.task_deleted,
                            taskViewModel.getTaskOrNull()?.description.orEmpty()
                        )
                    )
                }
                finishAffinity()
                startActivity(MainActivity.intentBuilder(this))
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
    }
}
