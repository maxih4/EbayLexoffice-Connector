package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaxConditions(
    @SerialName("taxType")
    val taxType: String? = ""
)