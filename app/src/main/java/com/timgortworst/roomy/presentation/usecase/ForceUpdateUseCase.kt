package com.timgortworst.roomy.presentation.usecase

interface ForceUpdateUseCase {
    fun onUpdateNeeded(updateUrl: String)
    fun onUpdateRecommended(updateUrl: String)
    fun noUpdateNeeded()
}