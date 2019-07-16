package com.timgortworst.roomy.ui.category.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.ui.BaseActivity
import com.timgortworst.roomy.ui.category.presenter.CategoryEditPresenter
import com.timgortworst.roomy.utils.Constants.INTENT_EXTRA_EDIT_HOUSEHOLD_TASK
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_edit_category.*
import javax.inject.Inject


class CategoryEditActivity : BaseActivity(), CategoryEditView {
    private lateinit var householdTask: EventCategory

    @Inject
    lateinit var presenter: CategoryEditPresenter

    companion object {
        fun start(context: AppCompatActivity) {
            val intent = Intent(context, CategoryEditActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.slide_up, R.anim.stay)
        }

        fun start(context: AppCompatActivity, householdTask: EventCategory) {
            val intent = Intent(context, CategoryEditActivity::class.java)
            intent.putExtra(INTENT_EXTRA_EDIT_HOUSEHOLD_TASK, householdTask)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.slide_up, R.anim.stay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        householdTask = intent.getParcelableExtra(INTENT_EXTRA_EDIT_HOUSEHOLD_TASK) ?: EventCategory()

        supportActionBar?.apply {
            title = "Nieuwe taak"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        householdTask.let {
            if (it.categoryId.isNotEmpty()) {
                supportActionBar?.title = "Edit  ${householdTask.name}"
                task_name_hint.editText?.setText(householdTask.name)
                task_description_hint.editText?.setText(householdTask.description)
            }
        }

        setupTextWachters()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_edit_done -> {
                presenter.insertOrUpdateCategory(
                    householdTask.categoryId,
                    task_name_hint.editText?.text.toString(),
                    task_description_hint.editText?.text.toString())

                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.stay, R.anim.slide_down)
    }

    private fun setupTextWachters() {
        task_name_hint.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                task_name_hint.error = null
            }
        })
        task_points_hint.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                task_points_hint.error = null
            }
        })
    }
}
