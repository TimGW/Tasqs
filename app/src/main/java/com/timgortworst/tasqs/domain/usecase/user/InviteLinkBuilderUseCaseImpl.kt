package com.timgortworst.tasqs.domain.usecase.user

import android.net.Uri
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.presentation.usecase.user.GetUserUseCase
import com.timgortworst.tasqs.presentation.usecase.user.InviteLinkBuilderUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class InviteLinkBuilderUseCaseImpl(
    private val getUserUseCase: GetUserUseCase
) : InviteLinkBuilderUseCase {

    override fun execute(params: Unit?) = flow {
        try {
            getUserUseCase.execute(GetUserUseCaseImpl.Params()).collect { response ->
                when(response) {
                    is Response.Success -> {
                        response.data?.householdId?.let {
                            val myUri = createShareUri(it)
                            val result = createDynamicUri(myUri)
                            emit(Response.Success(result))
                        } ?: emit(Response.Error())
                    }
                }
            }
        } catch (e: NumberFormatException) {
            emit(Response.Error())
        }
    }.flowOn(Dispatchers.Default)

    private fun createShareUri(householdId: String): Uri {
        val builder = Uri.Builder()
        builder.scheme("https")
            .authority("tasqs.page.link")
            .appendQueryParameter(QUERY_PARAM_HOUSEHOLD, householdId)
        return builder.build()
    }

    private fun createDynamicUri(myUri: Uri): Uri {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(myUri)
            .setDomainUriPrefix("https://tasqs.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .build()
            )
            .buildDynamicLink()
        return dynamicLink.uri
    }

    companion object {
        const val QUERY_PARAM_HOUSEHOLD = "QUERY_PARAM_HOUSEHOLD"
    }
}

