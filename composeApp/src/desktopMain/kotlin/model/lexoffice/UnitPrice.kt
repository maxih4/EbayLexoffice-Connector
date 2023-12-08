package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnitPrice(
    @SerialName("currency")
    val currency: String? = "",
    @SerialName("netAmount")
    val netAmount: Float? = 0F,
    @SerialName("grossAmount")
    val grossAmount: Float? = 0F,
    @SerialName("taxRatePercentage")
    val taxRatePercentage: Int? = 0

)