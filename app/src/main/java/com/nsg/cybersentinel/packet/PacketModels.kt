package com.nsg.cybersentinel.packet

data class IpPacketMetadata(
    val version: Int,
    val sourceAddress: String,
    val destinationAddress: String,
    val protocol: Int,
    val packetLength: Int,
    val headerLength: Int,
    val transportOffset: Int,
    val timestampMs: Long,
    val fragmented: Boolean,
)

data class TcpMetadata(
    val sourcePort: Int,
    val destinationPort: Int,
    val syn: Boolean,
    val ack: Boolean,
    val fin: Boolean,
    val rst: Boolean,
    val payloadLength: Int,
)

data class UdpMetadata(
    val sourcePort: Int,
    val destinationPort: Int,
    val datagramLength: Int,
    val payloadOffset: Int,
)

data class DnsMetadata(
    val queryName: String?,
    val queryType: Int?,
    val responseCode: Int,
    val answerCount: Int,
)
