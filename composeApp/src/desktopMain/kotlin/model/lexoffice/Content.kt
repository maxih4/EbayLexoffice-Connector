package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    @SerialName("addresses")
    val addresses: Addresses? = Addresses(),
    @SerialName("archived")
    val archived: Boolean? = false,
    @SerialName("emailAddresses")
    val emailAddresses: EmailAddresses? = EmailAddresses(),
    @SerialName("id")
    val id: String? = "",
    @SerialName("organizationId")
    val organizationId: String? = "",
    @SerialName("person")
    val person: Person? = Person(),
    @SerialName("roles")
    val roles: Roles? = Roles(),
    @SerialName("version")
    val version: Int? = 0
)