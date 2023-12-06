package model
import kotlinx.serialization.Serializable


@Serializable
data class UserAccessTokenResponse(
    val access_token: String = "",
    val expires_in: Int = 0,
    val refresh_token: String = "",
    val refresh_token_expires_in: Int = 0,
    val token_type: String = ""
)