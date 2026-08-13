package com.nsg.cybersentinel.runtime

import com.nsg.cybersentinel.model.SentinelSnapshot

object SentinelRuntime {
    @Volatile
    var snapshot: SentinelSnapshot = SentinelSnapshot()
        private set

    fun update(value: SentinelSnapshot) {
        snapshot = value
    }
}
