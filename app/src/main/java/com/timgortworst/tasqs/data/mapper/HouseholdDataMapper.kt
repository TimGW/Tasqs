package com.timgortworst.tasqs.data.mapper

import com.timgortworst.tasqs.domain.model.Household
import com.timgortworst.tasqs.domain.model.Task

class HouseholdDataMapper : Mapper<Map<String, Any>, Household> {

    override fun mapOutgoing(domain: Household): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        result[HOUSEHOLD_ID_REF] = domain.householdId
        return result
    }

    override fun mapIncoming(network: Map<String, Any>): Household {
        return Household(network[HOUSEHOLD_ID_REF] as String)
    }

    companion object {
        const val HOUSEHOLD_COLLECTION_REF = "households"
        const val HOUSEHOLD_ID_REF = "id"
    }
}