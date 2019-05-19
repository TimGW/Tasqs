package com.timgortworst.roomy.ui.setup.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.ui.setup.presenter.SetupPresenter
import com.timgortworst.roomy.ui.splash.ui.SplashActivity
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

        referredHouseholdId = intent.getStringExtra(referredHouseholdIdKey)

        presenter.setupHousehold(referredHouseholdId)
    }

    override fun presentToastError(error: Int) {
        Toast.makeText(this, getString(error), Toast.LENGTH_LONG).show()
    }

    override fun goToMainActivity() {
        MainActivity.start(this)
        finish()
    }

    override fun presentHouseholdOverwriteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Household overwrite")
            .setMessage("Your current household will be overwritten. All data will be lost. Are you sure?")
            .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                presenter.updateHousehold(referredHouseholdId)
            })
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    override fun restartApplication() {
        finishAffinity()
        SplashActivity.start(this)
    }

}
