package com.timgortworst.roomy.presentation.features.category.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.category.presenter.CategoryEditPresenter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_edit_category.*
import javax.inject.Inject

class CategoryEditActivity : BaseActivity(), CategoryEditView {
    private var category: Category? = null

    @Inject
    lateinit var presenter: CategoryEditPresenter

    companion object {
        const val INTENT_EXTRA_EDIT_CATEGORY = "INTENT_EXTRA_EDIT_CATEGORY"

        fun start(context: AppCompatActivity) {
            val intent = Intent(context, CategoryEditActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.slide_up, R.anim.stay)
        }

        fun start(context: AppCompatActivity, category: Category) {
            val intent = Intent(context, CategoryEditActivity::class.java)
            intent.putExtra(INTENT_EXTRA_EDIT_CATEGORY, category)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.slide_up, R.anim.stay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        category = intent.getParcelableExtra(INTENT_EXTRA_EDIT_CATEGORY)

        supportActionBar?.apply {
            title = getString(R.string.toolbar_title_new_category)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        category?.let {
            supportActionBar?.title =  getString(R.string.toolbar_title_edit_event, it.name)
            name_hint.editText?.setText(it.name)
            description_hint.editText?.setText(it.description)
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
                        category?.categoryId,
                        name_hint.editText?.text.toString(),
                        description_hint.editText?.text.toString())

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
        name_hint.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                name_hint.error = null
            }
        })
    }
}
