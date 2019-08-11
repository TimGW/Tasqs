package com.timgortworst.roomy.utils

object Constants {
    const val SHARED_PREF_FIRST_LAUNCH = "SHARED_PREF_FIRST_LAUNCH"
    const val SHARED_PREF_DARK_MODE = "SHARED_PREF_DARK_MODE"

    const val QUERY_PARAM_HOUSEHOLD = "QUERY_PARAM_HOUSEHOLD"

    const val HOUSEHOLD_COLLECTION_REF = "households"
    const val HOUSEHOLD_ID_REF = "householdId"
    const val HOUSEHOLD_BLACKLIST_REF = "userIdBlackList"

    const val USER_COLLECTION_REF = "users"
    const val USER_NAME_REF = "name"
    const val USER_EMAIL_REF = "email"
    const val USER_ROLE_REF = "role"
    const val USER_HOUSEHOLDID_REF = "householdId"

    const val CATEGORY_COLLECTION_REF = "categories"
    const val CATEGORY_ID_REF = "categoryId"
    const val CATEGORY_NAME_REF = "name"
    const val CATEGORY_DESCRIPTION_REF = "description"
    const val CATEGORY_HOUSEHOLDID_REF = "householdId"

    const val EVENT_COLLECTION_REF = "events"
    const val EVENT_CATEGORY_REF = "eventCategory"
    const val EVENT_INTERVAL_REF = "repeatInterval"
    const val EVENT_START_DATE_REF = "nextEventDate"
    const val EVENT_USER_REF = "user"
    const val EVENT_META_DATA_REF = "eventMetaData"
    const val EVENT_HOUSEHOLD_ID_REF = "householdId"

    const val LOADING_SPINNER_DELAY = 200L
    const val DEFAULT_HOUR_OF_DAY_NOTIFICATION = 20

}
