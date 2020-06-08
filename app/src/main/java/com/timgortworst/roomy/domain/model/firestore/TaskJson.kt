package com.timgortworst.roomy.domain.model.firestore

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.timgortworst.roomy.domain.model.TaskUser

@IgnoreExtraProperties
@Keep
data class TaskJson(
    @JvmField @PropertyName(TASK_ID_REF) var id: String? = null,
    @JvmField @PropertyName(TASK_DESCRIPTION_REF) var description: String? = null,
    @JvmField @PropertyName(TASK_META_DATA_REF) var metaData: TaskMetaDataJson? = TaskMetaDataJson(),
    @JvmField @PropertyName(TASK_USER_REF) var user: TaskUser? = null,
    @JvmField @PropertyName(TASK_HOUSEHOLD_ID_REF) var householdId: String? = null
) {
    companion object {
        const val TASK_ID_REF = "id"
        const val TASK_COLLECTION_REF = "tasks"
        const val TASK_USER_REF = "user"
        const val TASK_DESCRIPTION_REF = "description"
        const val TASK_META_DATA_REF = "meta_data"
        const val TASK_HOUSEHOLD_ID_REF = "household_id"
    }
}
