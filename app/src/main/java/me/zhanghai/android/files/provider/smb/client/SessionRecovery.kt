/*
 * Copyright (c) 2026 FM Plus Ultra contributors
 * SPDX-License-Identifier: GPL-3.0-only
 * Added 2026-10-04 for FM Plus Ultra.
 */
package me.zhanghai.android.files.provider.smb.client

import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.session.Session
import java.io.IOException

// Caller holds the session-cache monitor. Evict before cleanup, even if disconnect fails.
internal fun <K> MutableMap<K, Session>.getConnectedSession(key: K): Session? {
    val session = this[key] ?: return null
    if (session.connection.isConnected) {
        return session
    }
    remove(key)
    // Session.close() sends LOGOFF and can wait 60 seconds on the already-dead socket.
    // Connection.close(true) skips remote session/tree shutdown and tears down transport locally.
    session.connection.closeForRecovery()
    return null
}

internal fun Connection.closeForRecovery() {
    try {
        close(true)
    } catch (_: IOException) {
        // The failed connection must not prevent a subsequent fresh connection attempt.
    }
}
