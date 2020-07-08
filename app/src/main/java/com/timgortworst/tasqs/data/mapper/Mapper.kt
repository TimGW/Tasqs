package com.timgortworst.tasqs.data.mapper

interface Mapper<Network, Domain> {
    fun mapIncoming(network: Network): Domain
    fun mapOutgoing(domain: Domain): Network
}