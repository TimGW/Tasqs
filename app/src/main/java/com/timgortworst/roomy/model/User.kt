package com.timgortworst.roomy.model

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class User(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var totalPoints: Int = 0,
    var role : String = Role.ADMIN.name,
    var householdId : String = "",
    var color : Int = UserColor.GREEN.ordinal) : Parcelable {

    enum class Role {
        ADMIN,
        USER;
    }

    enum class UserColor(color: Int) {
        RED(Color.RED),
        BLACK(Color.BLACK),
        GREEN(Color.GREEN),
        BLUE(Color.BLUE),
        CYAN(Color.CYAN),
        YELLOW(Color.YELLOW)
    }
}
