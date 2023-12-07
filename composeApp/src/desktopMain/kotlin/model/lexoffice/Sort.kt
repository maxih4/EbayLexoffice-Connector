package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sort(
    @SerialName("ascending")
    val ascending: Boolean? = false,
    @SerialName("direction")
    val direction: String? = "",
    @SerialName("ignoreCase")
    val ignoreCase: Boolean? = false,
    @SerialName("nullHandling")
    val nullHandling: String? = "",
    @SerialName("property")
    val `property`: String? = ""
)