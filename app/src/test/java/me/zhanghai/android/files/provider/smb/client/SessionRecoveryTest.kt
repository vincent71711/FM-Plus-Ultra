/*
 * Copyright (c) 2026 FM Plus Ultra contributors
 * SPDX-License-Identifier: GPL-3.0-only
 * Added 2026-10-04 for FM Plus Ultra.
 */
package me.zhanghai.android.files.provider.smb.client

import com.hierynomus.smbj.SmbConfig
import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.event.SMBEventBus
import com.hierynomus.smbj.session.Session
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class SessionRecoveryTest {
    private class FakeConnection(var connected: Boolean) :
        Connection(SmbConfig.builder().build(), null, SMBEventBus(), null) {
        var forcedCloses = 0
        var failClose = false
        var onClose: () -> Unit = {}
        override fun isConnected(): Boolean = connected
        override fun close() { error("Graceful close would wait on the dead server") }
        override fun close(force: Boolean) {
            check(force) { "Recovery must skip network logoff" }
            ++forcedCloses
            onClose()
            if (failClose) throw IOException("synthetic disconnect failure")
        }
    }

    private fun session(connection: Connection): Session =
        object : Session(connection, null, null, null, null, null, null) {
            override fun close() { error("Session.close would send LOGOFF") }
        }

    @Test
    fun disconnectedSessionIsEvictedBeforeForcedCleanup() {
        val connection = FakeConnection(false)
        val cache = mutableMapOf("server" to session(connection))
        connection.onClose = { assertFalse(cache.containsKey("server")) }
        assertNull(cache.getConnectedSession("server"))
        assertEquals(1, connection.forcedCloses)
        val replacement = session(FakeConnection(true))
        cache["server"] = replacement
        assertSame(replacement, cache.getConnectedSession("server"))
    }

    @Test
    fun failedDisconnectDoesNotKeepDeadSessionOrBlockReplacement() {
        val connection = FakeConnection(false).apply { failClose = true }
        val cache = mutableMapOf("server" to session(connection))
        assertNull(cache.getConnectedSession("server"))
        assertTrue(cache.isEmpty())
        assertEquals(1, connection.forcedCloses)
    }

    @Test
    fun healthySessionIsReusedWithoutClosing() {
        val connection = FakeConnection(true)
        val original = session(connection)
        val cache = mutableMapOf("server" to original)
        assertSame(original, cache.getConnectedSession("server"))
        assertEquals(0, connection.forcedCloses)
    }
}
