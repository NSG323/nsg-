package com.nsg.cybersentinel.packet

object TcpParser {
    fun parse(bytes: ByteArray, ip: IpPacketMetadata): TcpMetadata? {
        val o = ip.transportOffset
        if (ip.fragmented || o + 20 > ip.packetLength || ip.packetLength > bytes.size) return null
        val src = u16(bytes, o)
        val dst = u16(bytes, o + 2)
        val dataOffset = ((bytes[o + 12].toInt() and 0xF0) ushr 4) * 4
        if (dataOffset < 20 || o + dataOffset > ip.packetLength) return null
        val flags = bytes[o + 13].toInt() and 0xFF
        return TcpMetadata(
            sourcePort = src,
            destinationPort = dst,
            syn = flags and 0x02 != 0,
            ack = flags and 0x10 != 0,
            fin = flags and 0x01 != 0,
            rst = flags and 0x04 != 0,
            payloadLength = (ip.packetLength - o - dataOffset).coerceAtLeast(0),
        )
    }
    private fun u16(b: ByteArray, i: Int): Int = ((b[i].toInt() and 0xFF) shl 8) or (b[i + 1].toInt() and 0xFF)
}

object UdpParser {
    fun parse(bytes: ByteArray, ip: IpPacketMetadata): UdpMetadata? {
        val o = ip.transportOffset
        if (ip.fragmented || o + 8 > ip.packetLength || ip.packetLength > bytes.size) return null
        val len = u16(bytes, o + 4)
        if (len < 8 || o + len > ip.packetLength) return null
        return UdpMetadata(
            sourcePort = u16(bytes, o),
            destinationPort = u16(bytes, o + 2),
            datagramLength = len,
            payloadOffset = o + 8,
        )
    }
    private fun u16(b: ByteArray, i: Int): Int = ((b[i].toInt() and 0xFF) shl 8) or (b[i + 1].toInt() and 0xFF)
}
