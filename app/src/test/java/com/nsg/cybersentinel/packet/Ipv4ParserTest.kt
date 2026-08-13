package com.nsg.cybersentinel.packet

import org.junit.Assert.*
import org.junit.Test

class Ipv4ParserTest {
    @Test fun parsesMinimalIpv4UdpHeader() {
        val b = ByteArray(28)
        b[0] = 0x45
        b[2] = 0
        b[3] = 28
        b[9] = 17
        b[12] = 10; b[13] = 0; b[14] = 0; b[15] = 1
        b[16] = 8; b[17] = 8; b[18] = 8; b[19] = 8
        b[20] = 0x30; b[21] = 0x39
        b[22] = 0; b[23] = 53
        b[24] = 0; b[25] = 8
        val ip = Ipv4Parser.parse(b)!!
        assertEquals("10.0.0.1", ip.sourceAddress)
        assertEquals("8.8.8.8", ip.destinationAddress)
        assertEquals(17, ip.protocol)
        val udp = UdpParser.parse(b, ip)!!
        assertEquals(12345, udp.sourcePort)
        assertEquals(53, udp.destinationPort)
    }

    @Test fun rejectsMalformedLength() {
        val b = ByteArray(20)
        b[0] = 0x45
        b[2] = 0
        b[3] = 40
        assertNull(Ipv4Parser.parse(b))
    }
}
