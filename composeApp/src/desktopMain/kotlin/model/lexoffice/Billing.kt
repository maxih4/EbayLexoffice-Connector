package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Billing(
    @SerialName("city")
    val city: String? = "",
    @SerialName("countryCode")
    val countryCode: String?="DE",
    @SerialName("street")
    val street: String? = "",
    @SerialName("supplement")
    val supplement: String? = "",
    @SerialName("zip")
    val zip: String? = ""
)