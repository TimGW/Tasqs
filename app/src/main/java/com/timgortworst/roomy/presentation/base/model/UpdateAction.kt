package com.timgortworst.roomy.presentation.base.model

sealed class UpdateAction {
    object none : UpdateAction()
    data class recommended(val url: String) : UpdateAction()
    data class required(val url: String) : UpdateAction()
}
