package com.timgortworst.roomy.ui.setup.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.timgortworst.roomy.R
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.ui.setup.presenter.SetupPresenter
import com.timgortworst.roomy.utils.AndroidUtil.closeKeyboard
import com.timgortworst.roomy.utils.AndroidUtil.openKeyboard
import com.timgortworst.roomy.utils.afterTextChanged
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_setup.*
import javax.inject.Inject


class SetupActivity : AppCompatActivity(), SetupView {

    @Inject
    lateinit var presenter: SetupPresenter
    lateinit var householdId: String

    companion object {
        const val householdIdKey = "householdIdKey"

        fun start(context: Context, householdId: String = "") {
            val intent = Intent(context, SetupActivity::class.java)
            intent.putExtra(householdIdKey, householdId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        householdId = intent.getStringExtra(householdIdKey)

        if (householdId.isNotBlank()) {
            setup_existing_household_code_text.setText(householdId)
            setup_existing_household_code_text.requestFocus()
            setup_existing_household_rb.isChecked = true
            setup_existing_household_code_hint.visibility = View.VISIBLE
        }

        setup_rb_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.setup_new_household_rb -> {
                    setup_existing_household_code_hint.visibility = View.INVISIBLE
                    closeKeyboard(this, setup_existing_household_code_text.windowToken)
                }
                R.id.setup_existing_household_rb -> {
                    setup_existing_household_code_hint.visibility = View.VISIBLE
                    openKeyboard(this, setup_existing_household_code_text)
                }
            }
        }

        setup_next_button.setOnClickListener {
            presenter.setupInitialHousehold(
                setup_new_household_rb.isChecked,
                setup_existing_household_rb.isChecked,
                setup_existing_household_code_text.text.toString()
            )
        }

        setup_existing_household_code_text.afterTextChanged { text ->
            setup_existing_household_code_hint.error = null
        }
    }

    override fun presentTextValidationError(errorStringResourceId: Int) {
        setup_existing_household_code_hint.error = getString(errorStringResourceId)
    }

    override fun presentToastError(error: Int) {
        Toast.makeText(this, getString(error), Toast.LENGTH_LONG).show()
    }

    override fun goToMainActivity() {
        MainActivity.start(this)
        finish()
    }
}
