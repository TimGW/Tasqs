package com.timgortworst.tasqs.data.mapper

class NullableOutputListMapperImpl<I, O>(
    private val mapper: Mapper<I, O>
) : NullableOutputListMapper<I, O> {

    override fun mapIncoming(network: List<I>): List<O>? {
        return if (network.isEmpty()) null else network.map { mapper.mapIncoming(it) }
    }

    override fun mapOutgoing(domain: List<O>?): List<I> {
        return domain?.map { mapper.mapOutgoing(it) }.orEmpty()
    }
}