package com.nsg.cybersentinel.vpn

import android.os.ParcelFileDescriptor
import android.net.VpnService

class TunInterfaceManager(private val service: VpnService) {
    private var tun: ParcelFileDescriptor? = null

    fun establishOnlyWhen(forwarding: ForwardingEngine): ParcelFileDescriptor? {
        if (!forwarding.ready) return null
        if (tun != null) return tun
        tun = service.Builder()
            .setSession("NSG Cyber Sentinel")
            .setMtu(1500)
            .addAddress("10.88.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .establish()
        return tun
    }

    fun close() {
        runCatching { tun?.close() }
        tun = null
    }
}
