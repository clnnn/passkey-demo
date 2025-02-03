package com.github.clnnn

import java.security.MessageDigest
import java.util.Base64

class PasskeyService {
    private val challengeStore = mutableMapOf<String, String>()
    private val userStore = mutableMapOf<String, UserData>()

    fun startRegistration(req: StartRegistrationRequest): StartRegistrationResponse {
        val username = req.username
        if (username.isBlank()) {
            return StartRegistrationResponse.Error("User name is required")
        }

        val challenge = generateRandomChallenge()
        challengeStore[username] = challenge
        return StartRegistrationResponse.Success(challenge)
    }

    fun finishRegistration(req: FinishRegistrationRequest): FinishRegistrationResponse {
        val (username, publicKey, credentialId) = req

        if (username.isBlank() || publicKey.isBlank() || credentialId.isBlank()) {
            return FinishRegistrationResponse.Error("Invalid request")
        }

        userStore[username] = UserData(username, publicKey, credentialId)
        return FinishRegistrationResponse.Success("Registration successful")
    }

    fun startAuthentication(req: StartAuthenticationRequest): StartAuthenticationResponse {
        val username = req.username
        val user = userStore[username]

        if (user == null) {
            return StartAuthenticationResponse.Error("User not found")
        }

        val challenge = generateRandomChallenge()
        challengeStore[username] = challenge
        return StartAuthenticationResponse.Success(challenge, user.credentialId)
    }

    fun finishAuthentication(req: FinishAuthenticationRequest): FinishAuthenticationResponse {
        val (username, challenge, signedChallenge, clientDataJSON, authenticatorData) = req
        val user = userStore[username]

        if (signedChallenge.isBlank() || clientDataJSON.isBlank() || authenticatorData.isBlank()) {
            return FinishAuthenticationResponse.Error("Invalid request")
        }

        if (challengeStore[username] != challenge) {
            return FinishAuthenticationResponse.Error("Invalid challenge")
        }

        if (user == null) {
            return FinishAuthenticationResponse.Error("User not found")
        }

        val isValid = verifySignature(
            publicKey = Base64.getDecoder().decode(user.publicKey),
            data = dataToVerify(authenticatorData, clientDataJSON),
            signature = Base64.getDecoder().decode(signedChallenge)
        )

        if (!isValid) {
            return FinishAuthenticationResponse.Error("Invalid signature")
        }

        return FinishAuthenticationResponse.Success("Authentication successful")
    }

    // Based on https://w3c.github.io/webauthn/#sctn-fido-u2f-sig-format-compat
    private fun dataToVerify(authenticatorData: String, clientDataJSON: String): ByteArray {
        val decodedAuthenticationData = Base64.getDecoder().decode(authenticatorData)
        val decodedClientDataJSON = Base64.getDecoder().decode(clientDataJSON)

        val md = MessageDigest.getInstance("SHA-256")
        val hashedClientDataJSON = md.digest(decodedClientDataJSON)

        return decodedAuthenticationData + hashedClientDataJSON
    }
}