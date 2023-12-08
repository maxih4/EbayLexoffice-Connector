package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShippingConditions(
    @SerialName("shippingDate")
    val shippingDate: String? = "",
    @SerialName("shippingType")
    val shippingType: String? = ""
)