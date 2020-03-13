package com.timgortworst.roomy.presentation.base.customview

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.BottomSheetMenuBinding
import com.timgortworst.roomy.domain.model.BottomMenuItem
import kotlinx.android.synthetic.main.bottom_sheet_menu.view.*

class BottomSheetMenu(
    context: Context,
    private val title: String,
    private val items: List<BottomMenuItem>
) {
    private val bottomSheetDialog: BottomSheetDialog =
        BottomSheetDialog(context)

    init {
        val binding = BottomSheetMenuBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog.setContentView(binding.root)

        with(binding) {
            bottomSheetTitle.text = title
            bottomSheetRecycler.layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            bottomSheetRecycler.adapter = BottomSheetMenuAdapter(items)
        }
    }

    fun show() {
        bottomSheetDialog.show()
    }

    fun dismiss() {
        bottomSheetDialog.dismiss()
    }
}