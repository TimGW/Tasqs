package com.timgortworst.roomy.repository

import com.timgortworst.roomy.model.UIState

interface ObjectStateListener {
    fun setUIState(uiState: UIState)
}
