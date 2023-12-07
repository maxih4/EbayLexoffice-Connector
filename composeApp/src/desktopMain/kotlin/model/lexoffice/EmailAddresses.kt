package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailAddresses(
    @SerialName("private")
    val `private`: List<String?>? = listOf()
)