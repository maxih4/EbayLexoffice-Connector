package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TotalPrice(
    @SerialName("currency")
    val currency: String? = ""
)