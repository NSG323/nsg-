package com.nsg.cybersentinel.flow

import java.util.LinkedHashMap

class FlowTable(
    private val maxEntries: Int = 4096,
    private val idleTimeoutMs: Long = 120_000L,
) {
    private val table = object : LinkedHashMap<FlowKey, FlowState>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<FlowKey, FlowState>?): Boolean = size > maxEntries
    }

    @Synchronized
    fun upsert(key: FlowKey, nowMs: Long, packetBytes: Int, outbound: Boolean, syn: Boolean = false, rst: Boolean = false): FlowState {
        expire(nowMs)
        val state = table.getOrPut(key) { FlowState(firstSeenMs = nowMs, lastSeenMs = nowMs) }
        state.lastSeenMs = nowMs
        if (outbound) {
            state.packetsOut += 1
            state.bytesOut += packetBytes.toLong()
        } else {
            state.packetsIn += 1
            state.bytesIn += packetBytes.toLong()
        }
        if (syn) state.synCount += 1
        if (rst) state.resetCount += 1
        return state
    }

    @Synchronized
    fun snapshot(): Map<FlowKey, FlowState> = table.mapValues { (_, v) -> v.copy() }

    @Synchronized
    fun size(): Int = table.size

    @Synchronized
    fun clear() = table.clear()

    @Synchronized
    fun expire(nowMs: Long) {
        val it = table.entries.iterator()
        while (it.hasNext()) {
            if (nowMs - it.next().value.lastSeenMs > idleTimeoutMs) it.remove()
        }
    }
}
