package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    @SerialName("city")
    val city: String? = "",
    @SerialName("countryCode")
    val countryCode: String? = "",
    @SerialName("name")
    val name: String? = "",
    @SerialName("street")
    val street: String? = "",
    @SerialName("supplement")
    val supplement: String? = "",
    @SerialName("zip")
    val zip: String? = "",
    @SerialName("contactId")
    val contactId: String?=""
)