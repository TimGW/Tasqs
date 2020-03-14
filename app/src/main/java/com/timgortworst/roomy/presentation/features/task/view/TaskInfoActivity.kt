package com.timgortworst.roomy.presentation.features.task.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.ActivityInfoTaskBinding
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.*

class TaskInfoActivity : BaseActivity() {
    private lateinit var binding: ActivityInfoTaskBinding
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
        binding = ActivityInfoTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.infoDescription.text = task.description
        binding.infoRepeated.text = buildRepeatText(task)
        if (task.user.name.isNotBlank()) {
            binding.userGroup.visibility = View.VISIBLE
            binding.infoUser.text = task.user.name
        }
        formatDate(task.metaData.startDateTime)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                parentActivityIntent?.let { NavUtils.navigateUpTo(this, it) }
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

    // todo extract to viewmodel
    private fun formatDate(zonedDateTime: ZonedDateTime) {
        val formattedDayOfMonth = zonedDateTime.dayOfMonth.toString()
        val formattedMonth = zonedDateTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val formattedYear = zonedDateTime.year.toString()
        binding.infoDate.text = "$formattedDayOfMonth $formattedMonth $formattedYear"
    }

    // todo extract to viewmodel
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

    // todo extract to viewmodel
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
