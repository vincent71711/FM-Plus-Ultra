/*
 * FM Plus Ultra modification (c) 2026 Vincent Frosceno
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package me.zhanghai.android.files.provider.smb.client

import com.hierynomus.security.AEADBlockCipher
import com.hierynomus.security.Cipher
import com.hierynomus.security.DerivationFunction
import com.hierynomus.security.Mac
import com.hierynomus.security.MessageDigest
import com.hierynomus.security.SecurityException
import com.hierynomus.security.SecurityProvider
import com.hierynomus.security.bc.BCSecurityProvider
import com.hierynomus.security.jce.JceSecurityProvider
import java.security.Provider
import java.security.Security

/**
 * Uses Android's native JCA/JCE implementations when the requested primitive is available.
 *
 * In particular, AndroidOpenSSL provides AES-CMAC used for SMB 3.x packet signing. SMBJ's default
 * provider performs that operation with the Bouncy Castle lightweight implementation, creating a
 * per-byte CPU ceiling during large transfers. Older NTLM primitives such as MD4 are not exposed by
 * AndroidOpenSSL, so every unavailable primitive transparently falls back to SMBJ's default.
 */
class AndroidSecurityProvider : SecurityProvider {
    // Bind only to Android's native Conscrypt/OpenSSL implementation. Using JCE's default lookup
    // here could silently select a registered Bouncy Castle provider and make the supposedly native
    // path another Java implementation. Each primitive is still attempted independently, so MD4
    // can fall back while AES-CMAC, HMAC and AES-GCM remain native on the same connection.
    private val platformProviders = Security.getProviders()
        .filter(::isNativeAndroidProvider)
        .map(::JceSecurityProvider)
    private val fallback = BCSecurityProvider()

    override fun getDigest(name: String): MessageDigest =
        preferPlatform({ it.getDigest(name) }, { fallback.getDigest(name) })

    override fun getMac(name: String): Mac =
        preferPlatform({ it.getMac(name) }, { fallback.getMac(name) })

    override fun getCipher(name: String): Cipher =
        preferPlatform({ it.getCipher(name) }, { fallback.getCipher(name) })

    override fun getAEADBlockCipher(name: String): AEADBlockCipher =
        preferPlatform(
            { it.getAEADBlockCipher(name) },
            { fallback.getAEADBlockCipher(name) }
        )

    override fun getDerivationFunction(name: String): DerivationFunction =
        preferPlatform(
            { it.getDerivationFunction(name) },
            { fallback.getDerivationFunction(name) }
        )

    private inline fun <T> preferPlatform(
        platformOperation: (JceSecurityProvider) -> T,
        fallbackOperation: () -> T
    ): T {
        for (platformProvider in platformProviders) {
            try {
                return platformOperation(platformProvider)
            } catch (_: SecurityException) {
                // Try another native provider, then the portable SMBJ implementation.
            }
        }
        return fallbackOperation()
    }

    private fun isNativeAndroidProvider(provider: Provider): Boolean {
        val providerName = provider.name.lowercase()
        val className = provider.javaClass.name.lowercase()
        return providerName == "androidopenssl" || providerName == "conscrypt" ||
            className.contains(".conscrypt.")
    }
}
