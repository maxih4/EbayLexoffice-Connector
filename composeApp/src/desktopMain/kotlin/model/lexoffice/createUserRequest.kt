package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class createUserRequest(
    @SerialName("addresses")
    val addresses: Addresses? = Addresses(),
    @SerialName("archived")
    val archived: Boolean? = false,
    @SerialName("emailAddresses")
    val emailAddresses: EmailAddresses? = EmailAddresses(),
    @SerialName("person")
    val person: Person? = Person(),
    @SerialName("roles")
    val roles: Roles? = Roles(),
    @SerialName("version")
    val version: Int? = 0
)