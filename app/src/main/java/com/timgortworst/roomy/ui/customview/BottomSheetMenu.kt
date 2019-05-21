package com.timgortworst.roomy.ui.customview

import android.content.Context
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.BottomMenuItem
import kotlinx.android.synthetic.main.bottom_sheet_menu.view.*

class BottomSheetMenu(
    private val context: Context,
    private val title: String,
    private val items: List<BottomMenuItem>
) {

    private val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_menu, null)
        bottomSheetDialog.setContentView(view)

        with(view) {
            bottom_sheet_title.text = title
            bottom_sheet_recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            bottom_sheet_recycler.adapter = BottomSheetMenuAdapter(items)
        }
    }

    fun show() {
        bottomSheetDialog.show()
    }

    fun dismiss() {
        bottomSheetDialog.dismiss()
    }
}