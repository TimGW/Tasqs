package com.timgortworst.roomy.ui.features.main.view

import android.net.Uri
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.roomy.data.utils.Constants

class InviteLink private constructor(val householdId: String) {

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
                    .appendQueryParameter(Constants.QUERY_PARAM_HOUSEHOLD, householdId)
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
}
