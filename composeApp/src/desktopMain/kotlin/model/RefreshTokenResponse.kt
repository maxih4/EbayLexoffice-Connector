package model

import kotlinx.serialization.Serializable

@Serializable

data class RefreshTokenResponse(
    val access_token: String,
    val expires_in: Int,
    val token_type: String
)