package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreationResponse(
    @SerialName("createdDate")
    val createdDate: String? = "",
    @SerialName("id")
    val id: String? = "",
    @SerialName("requestId")
    val requestId: String? = "",
    @SerialName("resourceUri")
    val resourceUri: String? = "",
    @SerialName("updatedDate")
    val updatedDate: String? = "",
    @SerialName("version")
    val version: Int? = 0
)