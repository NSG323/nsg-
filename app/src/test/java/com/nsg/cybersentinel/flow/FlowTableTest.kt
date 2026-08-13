package com.nsg.cybersentinel.flow

import org.junit.Assert.assertEquals
import org.junit.Test

class FlowTableTest {
    @Test fun remainsBounded() {
        val table = FlowTable(maxEntries = 3, idleTimeoutMs = Long.MAX_VALUE)
        repeat(10) { i ->
            table.upsert(FlowKey(17, "10.0.0.1", i, "8.8.8.8", 53), i.toLong(), 100, true)
        }
        assertEquals(3, table.size())
    }

    @Test fun expiresIdleFlows() {
        val table = FlowTable(maxEntries = 10, idleTimeoutMs = 100)
        table.upsert(FlowKey(6, "a", 1, "b", 2), 0, 50, true)
        table.expire(101)
        assertEquals(0, table.size())
    }
}
