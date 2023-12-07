package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    @SerialName("firstName")
    val firstName: String? = "",
    @SerialName("lastName")
    val lastName: String? = "",
    @SerialName("salutation")
    val salutation: String? = ""
)