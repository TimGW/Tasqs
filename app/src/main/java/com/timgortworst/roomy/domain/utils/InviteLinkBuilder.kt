package com.timgortworst.roomy.domain.utils

import android.net.Uri
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class InviteLinkBuilder private constructor(val householdId: String) {

    class Builder {
        private lateinit var householdId: String

        fun householdId(householdId: String) = apply { this.householdId = householdId }

        fun build(): Uri {
            val myUri = createShareUri(householdId)
            return createDynamicUri(myUri)
        }

        private fun createShareUri(householdId: String): Uri {
            val builder = Uri.Builder()
            builder.scheme("https")
                    .authority("roomyinvite.page.link")
                    .appendQueryParameter(QUERY_PARAM_HOUSEHOLD, householdId)
            return builder.build()
        }

        private fun createDynamicUri(myUri: Uri): Uri {
            val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(myUri)
                    .setDomainUriPrefix("https://roomyinvite.page.link")
                    .setAndroidParameters(
                            DynamicLink.AndroidParameters.Builder()
                                    .build()
                    )
                    .buildDynamicLink()
            return dynamicLink.uri
        }
    }

    companion object {
        const val QUERY_PARAM_HOUSEHOLD = "QUERY_PARAM_HOUSEHOLD"
    }
}
