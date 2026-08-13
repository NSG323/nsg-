package com.nsg.cybersentinel.packet

object DnsMetadataParser {
    fun parse(bytes: ByteArray, offset: Int, length: Int): DnsMetadata? {
        if (length < 12 || offset < 0 || offset + length > bytes.size) return null
        val flags = u16(bytes, offset + 2)
        val qd = u16(bytes, offset + 4)
        val an = u16(bytes, offset + 6)
        val rcode = flags and 0x000F
        var queryName: String? = null
        var queryType: Int? = null
        if (qd > 0) {
            val decoded = readName(bytes, offset + 12, offset + length) ?: return null
            queryName = decoded.first
            val pos = decoded.second
            if (pos + 4 <= offset + length) queryType = u16(bytes, pos)
        }
        return DnsMetadata(queryName, queryType, rcode, an)
    }

    private fun readName(bytes: ByteArray, start: Int, end: Int): Pair<String, Int>? {
        var p = start
        val labels = ArrayList<String>(8)
        var guard = 0
        while (p < end && guard++ < 64) {
            val n = bytes[p].toInt() and 0xFF
            if (n == 0) return labels.joinToString(".") to (p + 1)
            if (n and 0xC0 == 0xC0) {
                // Compression in a question name is valid but requires base-message offset tracking.
                // Fail closed rather than mis-parsing attribution metadata.
                return null
            }
            if (n > 63 || p + 1 + n > end) return null
            val label = bytes.copyOfRange(p + 1, p + 1 + n).toString(Charsets.US_ASCII)
            labels += label
            p += n + 1
        }
        return null
    }

    private fun u16(b: ByteArray, i: Int): Int = ((b[i].toInt() and 0xFF) shl 8) or (b[i + 1].toInt() and 0xFF)
}
