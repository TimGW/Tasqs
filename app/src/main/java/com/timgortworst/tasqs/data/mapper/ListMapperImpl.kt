package com.timgortworst.tasqs.data.mapper

class ListMapperImpl<I, O>(
    private val mapper: Mapper<I, O>
) : ListMapper<I, O> {
    override fun mapIncoming(network: List<I>): List<O> {
        return network.map { mapper.mapIncoming(it) }
    }

    override fun mapOutgoing(domain: List<O>): List<I> {
        return domain.map { mapper.mapOutgoing(it) }
    }
}