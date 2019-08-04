package com.timgortworst.roomy.ui.setup.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.timgortworst.roomy.R
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.ui.setup.presenter.SetupPresenter
import com.timgortworst.roomy.utils.showToast
import dagger.android.AndroidInjection
import javax.inject.Inject


class SetupActivity : AppCompatActivity(), SetupView {
    @Inject
    lateinit var presenter: SetupPresenter

    private lateinit var referredHouseholdId: String

    companion object {
        const val referredHouseholdIdKey = "referredHouseholdIdKey"

        fun start(context: Context, referredHouseholdId: String = "") {
            val intent = Intent(context, SetupActivity::class.java)
            intent.putExtra(referredHouseholdIdKey, referredHouseholdId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        referredHouseholdId = intent.getStringExtra(referredHouseholdIdKey) ?: ""
        presenter.setupHousehold(referredHouseholdId)
    }

    override fun presentToastError(error: Int) {
        showToast(error)
    }

    override fun goToMainActivity() {
        MainActivity.start(this)
        finish()
    }

    override fun presentHouseholdOverwriteDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_household_overwrite_title))
            .setMessage(getString(R.string.dialog_household_overwrite_text))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                presenter.changeCurrentUserHousehold(referredHouseholdId)
            }
            .setNegativeButton(android.R.string.no) { _, _ ->
                goToMainActivity()
            }
            .show()
    }

    override fun presentAlreadyInHouseholdDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_household_similar_title))
                .setMessage(getString(R.string.dialog_household_similar_text))
                .setNeutralButton(android.R.string.yes) { _, _ ->
                    goToMainActivity()
                }
                .show()
    }

    override fun presentUserIsBannedDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_household_banned_title))
                .setMessage(getString(R.string.dialog_household_banned_text))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    presenter.setupHousehold("")
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    finish()
                }
                .show()
    }
}
