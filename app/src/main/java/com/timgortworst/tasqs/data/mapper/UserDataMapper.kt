package com.timgortworst.tasqs.data.mapper

import com.timgortworst.tasqs.domain.model.User

class UserDataMapper : Mapper<Map<String, Any>, User?> {

    override fun mapOutgoing(domain: User?): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        domain?.let {
            result[USER_ID_REF] = domain.userId
            result[USER_NAME_REF] = domain.name
            result[USER_EMAIL_REF] = domain.email
            result[USER_ADMIN_REF] = domain.isAdmin
            result[USER_HOUSEHOLD_ID_REF] = domain.householdId
        }
        return result
    }

    override fun mapIncoming(network: Map<String, Any>): User? {
        val id = network[USER_ID_REF] as? String ?: return null
        val name = network[USER_NAME_REF] as? String ?: ""
        val email = network[USER_EMAIL_REF] as? String ?: ""
        val isAdmin = network[USER_ADMIN_REF] as? Boolean ?: false
        val householdId = network[USER_HOUSEHOLD_ID_REF] as? String ?: return null

        return User(id, name, email, isAdmin, householdId)
    }

    companion object {
        const val USER_COLLECTION_REF = "users"
        const val USER_ID_REF = "id"
        const val USER_NAME_REF = "name"
        const val USER_EMAIL_REF = "email"
        const val USER_ADMIN_REF = "is_admin"
        const val USER_HOUSEHOLD_ID_REF = "household_id"
    }
}