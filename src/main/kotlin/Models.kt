package com.github.clnnn

import kotlinx.serialization.Serializable

@Serializable
data class StartRegistrationRequest(val username: String)

@Serializable
data class FinishRegistrationRequest(val username: String, val publicKey: String, val credentialId: String)

@Serializable
data class StartAuthenticationRequest(val username: String)

@Serializable
data class FinishAuthenticationRequest(val username: String,
                                       val challenge: String,
                                       val signedChallenge: String,
                                       val clientDataJSON: String,
                                       val authenticatorData: String)

data class UserData(val username: String, val publicKey: String, val credentialId: String)

sealed class StartRegistrationResponse {
    data class Success(val challenge: String) : StartRegistrationResponse()
    data class Error(val message: String) : StartRegistrationResponse()
}

sealed class FinishRegistrationResponse {
    data class Success(val message: String) : FinishRegistrationResponse()
    data class Error(val message: String) : FinishRegistrationResponse()
}

sealed class StartAuthenticationResponse {
    data class Success(val challenge: String, val credentialId: String) : StartAuthenticationResponse()
    data class Error(val message: String) : StartAuthenticationResponse()
}

sealed class FinishAuthenticationResponse {
    data class Success(val message: String) : FinishAuthenticationResponse()
    data class Error(val message: String) : FinishAuthenticationResponse()
}