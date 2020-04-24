package com.timgortworst.roomy.presentation.usecase.settings

interface ForceUpdateUseCase {
    fun onUpdateNeeded(updateUrl: String)
    fun onUpdateRecommended(updateUrl: String)
    fun noUpdateNeeded()
}