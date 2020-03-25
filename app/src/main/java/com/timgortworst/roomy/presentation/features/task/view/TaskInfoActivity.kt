package com.timgortworst.roomy.presentation.features.task.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.NavUtils
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.ActivityInfoTaskBinding
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.presentation.base.view.BaseActivity

class TaskInfoActivity : BaseActivity() {
    private lateinit var binding: ActivityInfoTaskBinding

    companion object {
        private const val INTENT_EXTRA_INFO_TASK = "INTENT_EXTRA_INFO_TASK"

        fun intentBuilder(context: Context, task: Task): Intent {
            val intent = Intent(context, TaskInfoActivity::class.java)
            intent.putExtra(INTENT_EXTRA_INFO_TASK, task)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(INTENT_EXTRA_INFO_TASK)) {
            binding.task = intent.getParcelableExtra(INTENT_EXTRA_INFO_TASK) as Task
        } else {
            finish()
        }

        supportActionBar?.apply {
            title = getString(R.string.toolbar_title_info_task)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                parentActivityIntent?.let { NavUtils.navigateUpTo(this, it) }
                true
            }
            R.id.action_go_to_edit -> {
                TaskEditActivity.start(this, binding.task)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.info_menu, menu)
        return true
    }
}
