package com.timgortworst.roomy.utils

object Constants {
    const val SHARED_PREF_FIRST_LAUNCH = "SHARED_PREF_FIRST_LAUNCH"
    const val SHARED_PREF_HOUSEHOLD_ID = "SHARED_PREF_HOUSEHOLD_ID"
    const val SHARED_PREF_DARK_MODE = "SHARED_PREF_DARK_MODE"
    const val SHARED_PREF_USER_ID = "SHARED_PREF_USER_ID"

    const val QUERY_PARAM_HOUSEHOLD = "QUERY_PARAM_HOUSEHOLD"

    const val INTENT_EXTRA_EDIT_HOUSEHOLD_TASK = "INTENT_EXTRA_EDIT_HOUSEHOLD_TASK"
    const val INTENT_EXTRA_EDIT_AGENDA_ITEM= "INTENT_EXTRA_EDIT_AGENDA_ITEM"

    const val HOUSEHOLD_COLLECTION_REF = "households"
    const val ACTIVE_HOUSEHOLD_COLLECTION_REF = "activeHouseholds"
    const val AGENDA_EVENT_CATEGORIES_COLLECTION_REF = "agendaEventCategories"
    const val AGENDA_EVENTS_COLLECTION_REF = "agendaEvents"
    const val USERS_COLLECTION_REF = "users"

    const val USER_NAME_REF = "name"
    const val USER_EMAIL_REF = "email"
    const val USER_TOTALPOINTS_REF = "totalPoints"
    const val USER_ROLE_REF = "role"
    const val USER_HOUSEHOLDID_REF = "householdId"

    const val EVENT_CATEGORY_ID_REF = "categoryId"
    const val EVENT_CATEGORY_NAME_REF = "name"
    const val EVENT_CATEGORY_DESC_REF = "description"
    const val EVENT_CATEGORY_POINTS_REF = "points"

    const val EVENT_ID_REF = "agendaId"
    const val EVENT_CATEGORY_REF = "eventCategory"
    const val EVENT_INTERVAL_REF = "repeatInterval"
    const val EVENT_START_DATE_REF = "repeatStartDate"
    const val EVENT_USER_REF = "user"
    const val EVENT_META_DATA_REF = "eventMetaData"
    const val EVENT_IS_DONE_REF = "done"
}
