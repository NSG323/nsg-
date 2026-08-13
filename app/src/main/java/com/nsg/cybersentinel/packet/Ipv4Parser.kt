package com.nsg.cybersentinel.packet

object Ipv4Parser {
    fun parse(bytes: ByteArray, length: Int = bytes.size, timestampMs: Long = System.currentTimeMillis()): IpPacketMetadata? {
        if (length < 20 || bytes.size < length) return null
        val versionIhl = bytes[0].toInt() and 0xFF
        val version = versionIhl ushr 4
        if (version != 4) return null
        val ihl = (versionIhl and 0x0F) * 4
        if (ihl < 20 || ihl > length) return null
        val totalLength = u16(bytes, 2)
        if (totalLength < ihl || totalLength > length) return null
        val protocol = bytes[9].toInt() and 0xFF
        val flagsFragment = u16(bytes, 6)
        val moreFragments = (flagsFragment and 0x2000) != 0
        val fragmentOffset = flagsFragment and 0x1FFF
        return IpPacketMetadata(
            version = 4,
            sourceAddress = ipv4(bytes, 12),
            destinationAddress = ipv4(bytes, 16),
            protocol = protocol,
            packetLength = totalLength,
            headerLength = ihl,
            transportOffset = ihl,
            timestampMs = timestampMs,
            fragmented = moreFragments || fragmentOffset != 0,
        )
    }

    private fun u16(b: ByteArray, i: Int): Int = ((b[i].toInt() and 0xFF) shl 8) or (b[i + 1].toInt() and 0xFF)
    private fun ipv4(b: ByteArray, i: Int): String = (0..3).joinToString(".") { (b[i + it].toInt() and 0xFF).toString() }
}
