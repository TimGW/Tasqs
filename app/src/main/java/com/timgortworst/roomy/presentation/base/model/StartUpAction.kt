package com.timgortworst.roomy.presentation.base.model

sealed class StartUpAction {
    object TriggerSignInFlow : StartUpAction()
    object TriggerMainFlow : StartUpAction()
    object DialogSameId : StartUpAction()
    data class DialogOverrideId(val id: String) : StartUpAction()
}
