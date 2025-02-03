package com.github.clnnn

import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import kotlin.random.Random

fun generateRandomChallenge(): String {
    val bytes = ByteArray(32)
    Random.nextBytes(bytes)
    return Base64.getEncoder().encodeToString(bytes)
}

fun verifySignature(publicKey: ByteArray, data: ByteArray, signature: ByteArray): Boolean {
    return try {
        val keySpec = X509EncodedKeySpec(publicKey)
        val keyFactory = KeyFactory.getInstance("EC")
        val publicKey = keyFactory.generatePublic(keySpec)

        val verifier = Signature.getInstance("SHA256withECDSA")
        verifier.initVerify(publicKey)

        verifier.update(data)
        verifier.verify(signature)
    } catch (_: Exception) {
        false
    }
}