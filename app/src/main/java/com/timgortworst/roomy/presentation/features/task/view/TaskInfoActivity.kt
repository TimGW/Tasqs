package com.timgortworst.roomy.presentation.features.task.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.task.presenter.TaskInfoPresenter
import com.timgortworst.roomy.presentation.features.main.MainActivity
import kotlinx.android.synthetic.main.activity_info_task.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class TaskInfoActivity : BaseActivity(), TaskInfoView {
    private val presenter: TaskInfoPresenter by inject {
        parametersOf(this)
    }
    private lateinit var task: Task

    companion object {
        const val INTENT_EXTRA_INFO_TASK = "INTENT_EXTRA_INFO_TASK"

        fun start(context: AppCompatActivity, task: Task) {
            val intent = Intent(context, TaskInfoActivity::class.java)
            intent.putExtra(INTENT_EXTRA_INFO_TASK, task)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_task)

        if (intent.hasExtra(INTENT_EXTRA_INFO_TASK)) {
            task = intent.getParcelableExtra(INTENT_EXTRA_INFO_TASK) as Task
        } else {
            finish()
        }

        supportActionBar?.apply {
            title = getString(R.string.toolbar_title_info_task)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        info_description.text = task.description
        info_repeated.text = buildRepeatText(task)
        if (task.user.name.isNotBlank()) {
            user_group.visibility = View.VISIBLE
            info_user.text = task.user.name
        }
        presenter.formatDate(task.metaData.startDateTime)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, MainActivity::class.java))
                true
            }
            R.id.action_go_to_edit -> {
                TaskEditActivity.start(this, task)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.info_menu, menu)
        return true
    }

    override fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String) {
        info_date.text = "$formattedDayOfMonth $formattedMonth $formattedYear"
    }

    private fun buildRepeatText(task: Task): String {
        with(task.metaData.recurrence) {
            return if (this is TaskRecurrence.SingleTask) {
                getString(R.string.is_not_repeated)
            } else {
                val weeklyAddon = if (this is TaskRecurrence.Weekly) " ${getString(R.string.on)} ${formatWeekdays(onDaysOfWeek)}" else ""
                val isRepeatedOn = getString(R.string.is_repeated)
                val msg = if (frequency > 1) {
                    "$isRepeatedOn $frequency ${getString(pluralName)}"
                } else {
                    "$isRepeatedOn ${getString(name)}"
                }.plus(weeklyAddon)
                msg
            }
        }
    }

    private fun formatWeekdays(daysOfWeek: List<Int>?): String {
        return daysOfWeek?.joinToString {
            when (it) {
                1 -> getString(R.string.repeat_mo)
                2 -> getString(R.string.repeat_tu)
                3 -> getString(R.string.repeat_we)
                4 -> getString(R.string.repeat_th)
                5 -> getString(R.string.repeat_fr)
                6 -> getString(R.string.repeat_sa)
                7 -> getString(R.string.repeat_su)
                else -> "?"
            }
        }.orEmpty()
    }
}
