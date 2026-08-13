package com.nsg.cybersentinel.vpn

/**
 * A TUN interface must not be activated until this contract has a connectivity-safe implementation.
 */
interface ForwardingEngine {
    val ready: Boolean
    fun start(): Boolean
    fun stop()
}

class ForwardingEngineUnavailable : ForwardingEngine {
    override val ready: Boolean = false
    override fun start(): Boolean = false
    override fun stop() = Unit
}
