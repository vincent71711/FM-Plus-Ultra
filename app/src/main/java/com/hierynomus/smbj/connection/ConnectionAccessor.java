/*
 * Copyright (c) 2026 Vincent Frosceno
 *
 * This file is part of the FM Plus Ultra modifications.
 */

package com.hierynomus.smbj.connection;

import androidx.annotation.NonNull;

public final class ConnectionAccessor {
    private ConnectionAccessor() {}

    public static int getAvailableCredits(@NonNull Connection connection) {
        return connection.sequenceWindow.available();
    }
}
