package com.nsg.cybersentinel.vpn

import android.net.VpnService
import java.net.DatagramSocket
import java.net.Socket

class SocketProtector(private val service: VpnService) {
    fun protect(socket: Socket): Boolean = service.protect(socket)
    fun protect(socket: DatagramSocket): Boolean = service.protect(socket)
}
