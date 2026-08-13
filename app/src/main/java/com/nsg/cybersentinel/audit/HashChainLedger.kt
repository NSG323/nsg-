package com.nsg.cybersentinel.audit

import android.content.Context
import java.io.File
import java.security.MessageDigest

class HashChainLedger(context: Context) {
    private val file = File(context.filesDir, "sentinel_audit.log")
    @Volatile private var head: String = readHead()

    @Synchronized
    fun append(timestampMs: Long, source: String, payload: String): String {
        val canonical = "$timestampMs|$source|$payload|$head"
        val next = sha256(canonical)
        file.appendText("$timestampMs\t$source\t$payload\t$head\t$next\n")
        head = next
        return next
    }

    fun head(): String = head

    @Synchronized
    fun purge() {
        if (file.exists()) file.delete()
        head = "GENESIS"
    }

    private fun readHead(): String = runCatching {
        if (!file.exists()) return@runCatching "GENESIS"
        file.useLines { seq -> seq.lastOrNull()?.substringAfterLast('\t') ?: "GENESIS" }
    }.getOrDefault("GENESIS")

    private fun sha256(s: String): String = MessageDigest.getInstance("SHA-256")
        .digest(s.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }
}
