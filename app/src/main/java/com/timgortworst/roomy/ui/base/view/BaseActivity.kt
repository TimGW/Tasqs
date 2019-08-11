package com.timgortworst.roomy.ui.base.view

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import com.timgortworst.roomy.R

open class BaseActivity : AppCompatActivity() {

    val progressDialog by lazy {
        ProgressDialog(this)
    }

    fun showProgressDialog() {
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }
}