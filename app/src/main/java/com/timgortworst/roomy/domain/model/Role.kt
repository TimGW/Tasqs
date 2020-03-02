package com.timgortworst.roomy.domain.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
enum class Role {
    NORMAL,
    ADMIN
}

